package org.example.apps.timesheet.processors

import org.apache.logging.log4j.LogManager
import org.example.apps.timesheet.config.ProcessConfig
import org.example.apps.timesheet.config.ReportType
import org.example.apps.timesheet.entries.Entry
import org.example.apps.timesheet.entries.EntryReader
import org.example.apps.timesheet.reports.CsvTimesheetReport
import org.example.apps.timesheet.reports.ExcelTimesheetReport
import org.example.apps.timesheet.reports.FileReportConfig
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Named

@Named
class TimesheetProcessor {
    private val log = LogManager.getLogger(javaClass)
    @Inject
    lateinit var entryReader: EntryReader
    @Inject
    lateinit var monthDataProcessor: MonthDataProcessor
    @Inject
    lateinit var csvTimesheetReport: CsvTimesheetReport
    @Inject
    lateinit var excelTimesheetReport: ExcelTimesheetReport

    fun process(config: ProcessConfig) {
        val entries = readEntries(config)
        log.debug("entries: {}", entries)

        val dayWorkDataList = monthDataProcessor.process(entries, config)
        dayWorkDataList.forEach(log::debug)

        config.reportFiles[ReportType.CSV]?.let {
            csvTimesheetReport.create(dayWorkDataList, FileReportConfig(it, config.reportEncoding))
        }
        config.reportFiles[ReportType.EXCEL]?.let {
            excelTimesheetReport.create(dayWorkDataList, FileReportConfig(it, config.reportEncoding))
        }
    }

    private fun readEntries(config: ProcessConfig): List<Entry> {
        val entries = mutableListOf<Entry>()
        for (input in config.entriesFiles) {
            val currentEntries = entryReader.read(input, config.entriesConfig!!)
            for (currentEntry in currentEntries) {
                val existingEntry = entries.find {
                    it.datetime == currentEntry.datetime && it.typeCode == currentEntry.typeCode }
                if (existingEntry == null) {
                    entries += currentEntry
                } else {
                    log.debug("Merging entries {} and {}", existingEntry, currentEntry)
                    existingEntry.apply {
                        type = currentEntry.type
                        remarks = currentEntry.remarks
                    }
                }
            }
        }
        return entries
    }
}