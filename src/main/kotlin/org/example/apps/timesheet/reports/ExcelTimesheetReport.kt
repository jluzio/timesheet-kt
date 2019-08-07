package org.example.apps.timesheet.reports

import org.apache.logging.log4j.LogManager
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.example.apps.timesheet.processors.DayWorkData
import org.example.apps.timesheet.reports.ExcelTimesheetReport.SheetCfg.CellDataType
import org.example.apps.timesheet.reports.ExcelTimesheetReport.SheetCfg.DataGroupType
import java.nio.file.Files
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import javax.inject.Named
import extensions.java.util.date.*
import org.example.apps.timesheet.TimesheetConstants
import extensions.org.apache.poi.*

@Named
class ExcelTimesheetReport : TimesheetReport<FileReportConfig> {
    private val log = LogManager.getLogger(javaClass)

    interface SheetCfg {
        object Rows {
            val DATE = 0
        }
        object Columns {
            val DATE = 0
            val WORK_HOURS = 1
            val BREAK = 2
            val REMARKS = 3
            val ENTER = 4
            val EXIT = 5
            val WORK_HOURS_FORMULA = 6
        }
        enum class CellDataType {
            NORMAL,
            DATE,
            TIME,
            FLOAT_NUMBER
        }
        enum class DataGroupType {
            DEFAULT,
            WORKDAY,
            WEEKEND,
            ABSENCE,
            DAY_OFF
        }
    }
    private object Formulas {
        fun dateHours(str: String): String = String.format("HOUR(%1\$s)+MINUTE(%1\$s)/%2\$s+SECOND(%1\$s)/%3\$s", str, TimeUnit.HOURS.toMinutes(1), TimeUnit.HOURS.toSeconds(1))
        fun minuteHours(str: String): String = "$str/${TimeUnit.HOURS.toMinutes(1)}"
        fun dateDiff(c1: String, c2: String): String = "$c1 - $c2"
    }
    private object Predicates {
        val isWeekendDay = Predicate<DayWorkData> { it.startDatetime!!.dayOfWeek >= DayOfWeek.SATURDAY }
        val isDayOff = Predicate<DayWorkData> { it.dayOff && !isWeekendDay.test(it) }
        val isAbsence = Predicate<DayWorkData> { it.absence && !isWeekendDay.test(it) }
        val isDayOffOrAbsence = Predicate<DayWorkData> { isAbsence.or(isDayOff).test(it) }
        val isWorkDay = Predicate<DayWorkData> { isWeekendDay.negate().and(isDayOffOrAbsence.negate()).test(it) }
    }

    override fun create(dayWorkDataList: List<DayWorkData>, config: FileReportConfig) {
        log.debug("$javaClass:create: $dayWorkDataList")
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM")

        val workbook = HSSFWorkbook()

        val stylesMap = createCellStylesMap(workbook)

        val sheetName: String? = dayWorkDataList.first()?.let { dateFormat.format(it.startDatetime) }
        val sheet = workbook.createSheet(sheetName)

        sheet.row(0) {
            cell(SheetCfg.Columns.DATE) { setCellValue("Date") }
            cell(SheetCfg.Columns.WORK_HOURS) { setCellValue("Work (h)") }
            cell(SheetCfg.Columns.BREAK) { setCellValue("Break") }
            cell(SheetCfg.Columns.REMARKS) { setCellValue("Remarks") }
            cell(SheetCfg.Columns.ENTER) { setCellValue("Enter") }
            cell(SheetCfg.Columns.EXIT) { setCellValue("Exit") }
            cell(SheetCfg.Columns.WORK_HOURS_FORMULA) { setCellValue("Work (hf)") }
        }
        val headerRow = sheet.createRow(0)

        for ((index, dayWorkData) in dayWorkDataList.withIndex()) {
            val dataGroupType: DataGroupType = when {
                Predicates.isWeekendDay.test(dayWorkData) -> DataGroupType.WEEKEND
                Predicates.isAbsence.test(dayWorkData) -> DataGroupType.ABSENCE
                Predicates.isDayOff.test(dayWorkData) -> DataGroupType.DAY_OFF
                else -> DataGroupType.WORKDAY
            }
            val cellStyles = object {
                val normal = stylesMap[getCellStyleKey(CellDataType.NORMAL, dataGroupType)]
                val date = stylesMap[getCellStyleKey(CellDataType.DATE, dataGroupType)]
                val time = stylesMap[getCellStyleKey(CellDataType.TIME, dataGroupType)]
                val floatNumber = stylesMap[getCellStyleKey(CellDataType.FLOAT_NUMBER, dataGroupType)]
            }

            sheet.row(index + 1) {
                cell(SheetCfg.Columns.DATE, cellStyles.date) { setCellValue(dayWorkData.startDatetime?.toDate()) }
                cell(SheetCfg.Columns.WORK_HOURS, cellStyles.floatNumber) { setCellValue(dayWorkData.workInMinutes.toDouble() / TimeUnit.HOURS.toMinutes(1)) }
                val breakCell = cell(SheetCfg.Columns.BREAK, cellStyles.normal) { setCellValue(dayWorkData.breakInMinutes.toDouble()) }
                cell(SheetCfg.Columns.REMARKS, cellStyles.normal) { setCellValue(dayWorkData.remarks.orEmpty()) }
                val enterCell = cell(SheetCfg.Columns.ENTER, cellStyles.time) { setCellValue(dayWorkData.startDatetime?.toDate()) }
                val exitCell = cell(SheetCfg.Columns.EXIT, cellStyles.time) { dayWorkData.exitDatetime?.run { setCellValue(toDate()) } }

                val workTimeNoBreakFormula = Formulas.dateDiff(exitCell.getRef(), enterCell.getRef())
                val workHoursFormula = "${Formulas.dateHours(workTimeNoBreakFormula)} - ${Formulas.minuteHours(breakCell.getRef())}"
                cell(SheetCfg.Columns.WORK_HOURS_FORMULA, cellStyles.floatNumber) { cellFormula = workHoursFormula }
            }
        }

        createSummary(dayWorkDataList, sheet, stylesMap)

        log.debug("Writing to {}", config.file)
        Files.newOutputStream(config.file).use {
            workbook.write(it);
            workbook.close();
        }
    }

    private fun createSummary(dayWorkDataList: List<DayWorkData>, sheet: Sheet, stylesMap: Map<String, CellStyle>) {
        /*
         * Summary of month
         */
        val summaryValues = object {
            val sumWorkInHours = dayWorkDataList.map { it.breakInMinutes }.sum().toDouble() / TimeUnit.HOURS.toMinutes(1)
            val workDaysInMonth = dayWorkDataList.count { Predicates.isWorkDay.test(it) }
            val workHoursInMonth = workDaysInMonth * TimesheetConstants.WORK_TIME_HOURS
            val missingWorkHours = workHoursInMonth - sumWorkInHours
            val expectedBreakHoursInMonth = workDaysInMonth * TimesheetConstants.BREAK_TIME_HOURS
            val totalBreakHoursInMonth = dayWorkDataList.map { it.breakInMinutes }.sum().toDouble() / TimeUnit.HOURS.toMinutes(1)
            val missingBreakHoursInMonth = expectedBreakHoursInMonth - totalBreakHoursInMonth
        }
        val dataIndexes = object {
            val valuesRow = 1
            val footerStartRow = dayWorkDataList.size + 1 + 2
            val workPerHourCol = SheetCfg.Columns.WORK_HOURS
            val breakPerHourCol = SheetCfg.Columns.BREAK
        }
        val summaryCellStyles = object {
            val floatNumber = stylesMap[getCellStyleKey(CellDataType.FLOAT_NUMBER, DataGroupType.DEFAULT)]
        }
        val cellRefs = object {
            val firstWorkHours = sheet.findCell(dataIndexes.valuesRow, dataIndexes.workPerHourCol).getRef()
            val lastWorkHours = sheet.findCell(dataIndexes.valuesRow + dayWorkDataList.lastIndex, dataIndexes.workPerHourCol).getRef()
            val firstBreakHours = sheet.findCell(dataIndexes.valuesRow, dataIndexes.breakPerHourCol).getRef()
            val lastBreakHours = sheet.findCell(dataIndexes.valuesRow + dayWorkDataList.lastIndex, dataIndexes.breakPerHourCol).getRef()
            lateinit var workDays: String
            lateinit var expectedWorkHours: String
            lateinit var workHours: String
            lateinit var workHoursFn: String
            lateinit var expectedBreakHours: String
            lateinit var breakHoursFn: String
        }
        val footerHeaderCol = dataIndexes.workPerHourCol - 1
        val footerValueCol = footerHeaderCol + 1

        var currentFooterRow = dataIndexes.footerStartRow
        sheet.row(currentFooterRow) {
            cell(footerHeaderCol) { setCellValue("WorkDays") }
            cell(footerValueCol, summaryCellStyles.floatNumber) {
                setCellValue(summaryValues.workDaysInMonth.toDouble())
                cellRefs.workDays = getRef()
            }
        }

        currentFooterRow++
        sheet.row(currentFooterRow) {
            cell(footerHeaderCol) { setCellValue("[E]Work") }
            cell(footerValueCol, summaryCellStyles.floatNumber) {
                cellFormula = "${TimesheetConstants.WORK_TIME_HOURS} * ${cellRefs.workDays}"
                cellRefs.expectedWorkHours = getRef()
            }
        }

        sheet.createSeparator(++currentFooterRow, footerHeaderCol)

        currentFooterRow++
        sheet.row(currentFooterRow) {
            cell(footerHeaderCol) { setCellValue("Work") }
            cell(footerValueCol, summaryCellStyles.floatNumber) {
                setCellValue(summaryValues.sumWorkInHours)
                cellRefs.workHours = getRef()
            }
        }

        currentFooterRow++
        sheet.row(currentFooterRow) {
            cell(footerHeaderCol) { setCellValue("Work(f)") }
            cell(footerValueCol, summaryCellStyles.floatNumber) {
                cellFormula = "SUM(${cellRefs.firstWorkHours} : ${cellRefs.lastWorkHours})"
                cellRefs.workHoursFn = getRef()
            }
        }

        currentFooterRow++
        sheet.row(currentFooterRow) {
            cell(footerHeaderCol) { setCellValue("[-]Work") }
            cell(footerValueCol, summaryCellStyles.floatNumber) {
                setCellValue(summaryValues.missingWorkHours)
            }
        }

        currentFooterRow++
        sheet.row(currentFooterRow) {
            cell(footerHeaderCol) { setCellValue("[-]Work(f)") }
            cell(footerValueCol, summaryCellStyles.floatNumber) {
                cellFormula = "${cellRefs.expectedWorkHours} - ${cellRefs.workHoursFn}"
            }
        }

        sheet.createSeparator(++currentFooterRow, footerHeaderCol)

        currentFooterRow++
        sheet.row(currentFooterRow) {
            cell(footerHeaderCol) { setCellValue("[E]Break") }
            cell(footerValueCol, summaryCellStyles.floatNumber) {
                cellFormula = "${TimesheetConstants.BREAK_TIME_HOURS} * ${cellRefs.workDays}"
                cellRefs.expectedBreakHours = getRef()
            }
        }

        currentFooterRow++
        sheet.row(currentFooterRow) {
            cell(footerHeaderCol) { setCellValue("Break(f)") }
            cell(footerValueCol, summaryCellStyles.floatNumber) {
                cellFormula = "SUM(${cellRefs.firstBreakHours} : ${cellRefs.lastBreakHours}) / ${TimeUnit.HOURS.toMinutes(1)}"
                cellRefs.breakHoursFn = getRef()
            }
        }

        currentFooterRow++
        sheet.row(currentFooterRow) {
            cell(footerHeaderCol) { setCellValue("[-]Break(f)") }
            cell(footerValueCol, summaryCellStyles.floatNumber) {
                cellFormula = "${cellRefs.expectedBreakHours} - ${cellRefs.breakHoursFn}"
            }
        }
    }

    private fun createCellStylesMap(workbook: Workbook): Map<String, CellStyle> {
        val stylesMap = mutableMapOf<String, CellStyle>()

        val dataFormatFactory = workbook.createDataFormat()

        val normalCellStyle = workbook.createCellStyle()
        val dateCellStyle = workbook.createCellStyle().apply { dataFormat = dataFormatFactory.getFormat("dd") }
        val timeCellStyle = workbook.createCellStyle().apply { dataFormat = dataFormatFactory.getFormat("HH:mm") }
        val floatNumberCellStyle = workbook.createCellStyle().apply { dataFormat = dataFormatFactory.getFormat("0.00") }

        val _k = this::getCellStyleKey

        stylesMap[_k(SheetCfg.CellDataType.NORMAL, SheetCfg.DataGroupType.DEFAULT)] = normalCellStyle
        stylesMap[_k(SheetCfg.CellDataType.DATE, SheetCfg.DataGroupType.DEFAULT)] = dateCellStyle
        stylesMap[_k(SheetCfg.CellDataType.TIME, SheetCfg.DataGroupType.DEFAULT)] = timeCellStyle
        stylesMap[_k(SheetCfg.CellDataType.FLOAT_NUMBER, SheetCfg.DataGroupType.DEFAULT)] = floatNumberCellStyle

        val workdayColor: Short? = null
        val weekendColor = IndexedColors.AQUA.getIndex()
        val dayOffColor = IndexedColors.LIGHT_GREEN.getIndex()
        val absenceColor = IndexedColors.YELLOW.getIndex()

        createStyleVariant(DataGroupType.WORKDAY, workdayColor, stylesMap, workbook)
        createStyleVariant(DataGroupType.WEEKEND, weekendColor, stylesMap, workbook)
        createStyleVariant(DataGroupType.ABSENCE, absenceColor, stylesMap, workbook)
        createStyleVariant(DataGroupType.DAY_OFF, dayOffColor, stylesMap, workbook)

        return stylesMap
    }

    private fun getCellStyleKey(cellDataType: SheetCfg.CellDataType, dataGroupType: SheetCfg.DataGroupType) = "$cellDataType:$dataGroupType"

    private fun createStyleVariant(dataGroupType: DataGroupType, color: Short?, stylesMap: MutableMap<String, CellStyle>, workbook: Workbook) {
        for (cellDataType in CellDataType.values()) {
            val defaultCellStyle = stylesMap[getCellStyleKey(cellDataType, DataGroupType.DEFAULT)]!!

            val variantCellStyle = if (color == null) defaultCellStyle else workbook.createCellStyle().copyStyle(defaultCellStyle).foregroundColor(color)
            val variantKey = getCellStyleKey(cellDataType, dataGroupType)
            stylesMap[variantKey] = variantCellStyle
        }
    }

    private fun CellStyle.foregroundColor(color: Short?): CellStyle {
        return apply {
            if (color != null) {
                fillForegroundColor = color
                fillPattern = FillPatternType.SOLID_FOREGROUND
            } else {
                fillForegroundColor = IndexedColors.AUTOMATIC.getIndex()
                fillPattern = FillPatternType.NO_FILL
            }
        }
    }

    private fun Sheet.createSeparator(row: Int, column: Int): Unit {
        row(row) {
            cell(column) {
                setCellValue("-".repeat(8))
            }
        }
    }

}