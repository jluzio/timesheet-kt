package org.example.apps.timesheet.processors

import org.example.apps.timesheet.AbstractTest
import org.example.apps.timesheet.config.EntriesConfigReader
import org.example.apps.timesheet.config.ReportType
import org.example.apps.timesheet.config.RunnerConfig
import org.junit.Test
import java.nio.file.Path
import java.time.LocalDate
import javax.inject.Inject

class TimesheetRunnerTest: AbstractTest() {
    @Inject
    lateinit var timesheetRunner: TimesheetRunner
    @Inject
    lateinit var entriesConfigReader: EntriesConfigReader

    @Test
    fun testCurrentFormat() {
        val loader = Thread.currentThread().contextClassLoader

        val inputFile = Path.of(loader.getResource("201706.xls").toURI())
        val entriesPath = inputFile.parent.toString()
        val reportsPath = entriesPath
        val monthDate = LocalDate.of(2017, 6, 1)
        val configDataPath = Path.of(loader.getResource("configData-test").toURI()).toString()
        val entriesConfigFile = Path.of(loader.getResource("entriesConfig-default.xml").toURI())
        val entriesConfig = entriesConfigReader.read(entriesConfigFile)

        val runnerConfig = RunnerConfig(
                targetDate = monthDate,
                entriesPath = entriesPath,
                entriesConfig = entriesConfig,
                configDataPath = configDataPath,
                reportsPath = reportsPath,
                reportEncoding = "UTF-8",
                fillAllMonthDays = true,
                reportTypes = ReportType.values().toList()
        )
        timesheetRunner.run(runnerConfig)
    }
}