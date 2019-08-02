package org.example.apps.timesheet.processors

import org.example.apps.timesheet.config.*
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

@Named
class TimesheetRunner {
    @Inject
    lateinit var timesheetProcessor: TimesheetProcessor
    @Inject
    lateinit var configDataReader: ConfigDataReader
    @Inject
    lateinit var entriesConfigReader: EntriesConfigReader
    @Inject
    lateinit var runnerConfigReader: RunnerConfigReader

    fun run(runnerConfig: RunnerConfig) {
        val monthDate = runnerConfig.targetDate?.withDayOfMonth(1) ?: LocalDate.now().withDayOfMonth(1)
        val dateFilenamePart = monthDate.format(DateTimeFormatter.ofPattern("yyyyMM"))

        val baseInputFilename = "$dateFilenamePart"
        val inputFile = Path.of(runnerConfig.entriesPath, "$baseInputFilename.xls")
        val customInputFile = Path.of(runnerConfig.entriesPath, "${baseInputFilename}_custom.xls")
        val entriesFiles = listOf(inputFile, customInputFile).filter { Files.exists(it) }

        val configDataDir = Path.of(runnerConfig.configDataPath)
        val configData = configDataReader.read(configDataDir)

        val baseReportFilename = "timesheet_$dateFilenamePart"
        val reportFiles = runnerConfig.reportTypes.associateWithTo(
                mutableMapOf<ReportType, Path>(),
                {
                    Path.of(runnerConfig.reportsPath, "$baseReportFilename.${getReportTypeExt(it)}")
                })
        val processConfig = ProcessConfig(
                entriesFiles = entriesFiles,
                entriesConfig = runnerConfig.entriesConfig,
                configData = configData,
                reportEncoding = runnerConfig.reportEncoding,
                fillAllMonthDays = runnerConfig.fillAllMonthDays,
                month = monthDate,
                reportFiles = reportFiles
        )

        timesheetProcessor.process(processConfig)
    }

    fun getRunnerConfig(appHomePath: Path): RunnerConfig {
        val loader = Thread.currentThread().contextClassLoader
        val defaultRunnerConfigFile = Path.of(loader.getResource("runnerConfig-default.xml").toURI())
        val defaultEntriesConfigFile = Path.of(loader.getResource("entriesConfig-default.xml").toURI())

        var configFile = appHomePath.resolve("timesheet.xml")
        if (!Files.exists(configFile)) {
            configFile = defaultRunnerConfigFile
        }
        val runnerConfig = runnerConfigReader.read(configFile)
        if (runnerConfig.entriesConfig == null) {
            runnerConfig.entriesConfig = entriesConfigReader.read(defaultEntriesConfigFile)
        }
        return runnerConfig
    }

    private fun getReportTypeExt(reportType: ReportType): String = when(reportType) {
        ReportType.EXCEL -> "xls"
        ReportType.CSV -> "csv"
    }

}