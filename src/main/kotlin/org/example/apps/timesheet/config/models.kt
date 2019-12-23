package org.example.apps.timesheet.config

import com.fasterxml.jackson.core.type.TypeReference
import java.nio.file.Path
import java.time.LocalDate
import java.util.*
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

const val DEFAULT_ENCODING = "UTF-8"

data class Absence(
        var date: LocalDate? = null,
        var endDate: LocalDate? = null,
        var type: AbsenceType? = null
)

data class DayOff(
        var name: String? = null,
        var date: LocalDate? = null,
        var type: DayOffType? = null
)

data class ConfigData(
        var absences: List<Absence> = emptyList(),
        var daysOff: List<DayOff> = emptyList()
)

@XmlRootElement
data class EntriesConfig(
        var encoding: String = "",
        var serviceExitText: String = "",
        var dateFormat: String = "",
        var dateTimeFormat: String = ""
)

data class ProcessConfig(
        var month: LocalDate? = null,
        var entriesFiles: List<Path> = emptyList(),
        var entriesConfig: EntriesConfig? = null,
        var configData: ConfigData? = null,
        var reportFiles: Map<ReportType, Path> = mapOf(),
        var reportEncoding: String = DEFAULT_ENCODING,
        var fillAllMonthDays: Boolean = true
)

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
data class RunnerConfig(
        var targetDate: LocalDate? = null,
        var entriesPath: String? = null,
        var entriesConfig: EntriesConfig? = null,
        var configDataPath: String? = null,
        var reportsPath: String? = null,
        var reportEncoding: String = DEFAULT_ENCODING,
        @field:XmlElement(name = "reportType")
        var reportTypes: List<ReportType> = mutableListOf(),
        var fillAllMonthDays: Boolean = true
)

enum class AbsenceType {
    VACATION, UNJUSTIFIED, OTHER
}
enum class DayOffType {
    HOLIDAY, OPTIONAL, OTHER
}
enum class ReportType {
    EXCEL, CSV
}

interface JsonTypeReferences {
    companion object {
        val ABSENCES: TypeReference<LinkedHashMap<AbsenceType, List<Absence>>> = object : TypeReference<LinkedHashMap<AbsenceType, List<Absence>>>() {
        }
        val DAYS_OFF: TypeReference<LinkedHashMap<DayOffType, List<DayOff>>> = object : TypeReference<LinkedHashMap<DayOffType, List<DayOff>>>() {
        }
    }
}
