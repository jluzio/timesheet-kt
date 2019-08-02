package org.example.apps.timesheet.reports

import org.apache.logging.log4j.LogManager
import org.example.apps.timesheet.processors.DayWorkData
import java.nio.file.Path
import javax.inject.Named
import java.util.function.BiFunction
import java.util.concurrent.TimeUnit
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.example.apps.timesheet.reports.ExcelTimesheetReport.SheetCfg.DataGroupType
import org.example.apps.timesheet.reports.ExcelTimesheetReport.SheetCfg.CellDataType
import java.nio.file.Files
import java.time.format.DateTimeFormatter


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
    object Formulas {
        fun dateHours(str: String): String = String.format("HOUR(%1\$s)+MINUTE(%1\$s)/%2\$s+SECOND(%1\$s)/%3\$s", str, TimeUnit.HOURS.toMinutes(1), TimeUnit.HOURS.toSeconds(1))
        fun minuteHours(str: String): String = "$str/${TimeUnit.HOURS.toMinutes(1)}"
        fun dateDiff(c1: String, c2: String): String = "$c1 - $c2"
    }

    override fun create(dayWorkDataList: List<DayWorkData>, config: FileReportConfig) {
        log.debug("$javaClass:create: $dayWorkDataList")
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM")

        val workbook = HSSFWorkbook()

        val stylesMap = createCellStylesMap(workbook)

        val sheetName: String? = dayWorkDataList.first()?.let { dateFormat.format(it.startDatetime) }
        val sheet = workbook.createSheet(sheetName)

        val headerRow = sheet.createRow(0)
        createCell(headerRow, SheetCfg.Columns.DATE).setCellValue("Date");
        createCell(headerRow, SheetCfg.Columns.WORK_HOURS).setCellValue("Work (h)");
        createCell(headerRow, SheetCfg.Columns.BREAK).setCellValue("Break");
        createCell(headerRow, SheetCfg.Columns.REMARKS).setCellValue("Remarks");
        createCell(headerRow, SheetCfg.Columns.ENTER).setCellValue("Enter");
        createCell(headerRow, SheetCfg.Columns.EXIT).setCellValue("Exit");
        createCell(headerRow, SheetCfg.Columns.WORK_HOURS_FORMULA).setCellValue("Work (hf)");

        Files.newOutputStream(config.file).use {
            workbook.write(it);
            workbook.close();
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

            val variantCellStyle = when(color) {
                null -> defaultCellStyle
                else -> createBGColorCellStyle(workbook.createCellStyle(), defaultCellStyle, color)
            }
            val variantKey = getCellStyleKey(cellDataType, dataGroupType)
            stylesMap[variantKey] = variantCellStyle
        }
    }

    private fun createBGColorCellStyle(cellStyle: CellStyle, fromCellStyle: CellStyle?, bg: Short?): CellStyle {
        cellStyle.cloneStyleFrom(fromCellStyle)
        setCellFill(cellStyle, bg)
        return cellStyle
    }

    private fun setCellFill(cellStyle: CellStyle, bg: Short?) {
        if (bg != null) {
            cellStyle.fillForegroundColor = bg
            cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        } else {
            cellStyle.fillForegroundColor = IndexedColors.AUTOMATIC.getIndex()
            cellStyle.fillPattern = FillPatternType.NO_FILL
        }
    }

    private fun createCell(row: Row, column: Int, cellStyle: CellStyle? = null): Cell {
        val c = row.createCell(column)
        if (cellStyle != null) {
            c.cellStyle = cellStyle
        }
        return c
    }

}