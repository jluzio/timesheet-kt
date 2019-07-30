package org.example.apps.timesheet.config

import java.io.File
import java.time.LocalDate
import javax.xml.bind.annotation.XmlRootElement

data class Absence(
        var date: LocalDate?,
        var endDate: LocalDate?,
        var type: AbsenceType?
)

data class DayOff(
        var name: String,
        var date: LocalDate?,
        var type: DayOffType?
)

data class ConfigData(
        val absences: List<Absence> = mutableListOf(),
        val daysOff: List<DayOff> = mutableListOf()
)

@XmlRootElement
data class EntriesConfig(
        var encoding: String? = null,
        var serviceExitText: String? = null,
        var dateFormat: String? = null,
        var dateTimeFormat: String? = null
)

data class ProcessConfig(
        var month: LocalDate? = null,
        var entriesFiles: List<File> = mutableListOf(),
        var entriesConfig: EntriesConfig = EntriesConfig(),
        var configData: ConfigData = ConfigData(),
        var reportFiles: Map<ReportType, File> = mutableMapOf(),
        var reportEncoding: String = "UTF-8",
        var fillAllMonthDays: Boolean = true
)

enum class AbsenceType {
    VACATION, UNJUSTIFIED, OTHER;
}

enum class DayOffType {
    HOLIDAY, OTHER
}

enum class ReportType {
    EXCEL, CSV
}
