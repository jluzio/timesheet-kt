package org.example.apps.timesheet.reports

import org.apache.logging.log4j.LogManager
import org.example.apps.timesheet.processors.DayWorkData
import java.nio.charset.Charset
import java.nio.file.Files
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Named
class CsvTimesheetReport : TimesheetReport<FileReportConfig> {
    private val log = LogManager.getLogger(javaClass)

    override fun create(dayWorkDataList: List<DayWorkData>, config: FileReportConfig) {
        val dateFormat = DateTimeFormatter.ofPattern("dd")
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

        var writer = Files.newBufferedWriter(config.file, Charset.forName(config.encoding))
        writer.use { w ->
            fun writeln(vararg values: Any) {
                w.write(values.joinToString(separator = "\t"))
                w.newLine()
            }
            writeln(
                    "Date", "Work", "Work (h)",
                    "Break", "Remarks", "Enter",
                    "Exit")
            dayWorkDataList.forEach {
                it.run {
                    writeln(
                            dateFormat.format(startDatetime),
                            workInMinutes,
                            workInMinutes.toFloat() / TimeUnit.HOURS.toMinutes(1),
                            breakInMinutes,
                            remarks ?: "",
                            timeFormat.format(startDatetime),
                            timeFormat.format(exitDatetime)
                    )
                }
            }
        }
    }
}
