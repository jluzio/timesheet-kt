package org.example.apps.timesheet.config

import com.fasterxml.jackson.core.type.TypeReference
import java.nio.file.Path
import java.time.LocalDate
import java.util.LinkedHashMap
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement

val defaultEncoding = "UTF-8"

data class Absence(
        var date: LocalDate?,
        var endDate: LocalDate?,
        var type: AbsenceType?
)

data class DayOff(
        var name: String?,
        var date: LocalDate?,
        var type: DayOffType?
)

data class ConfigData(
        var absences: List<Absence> = listOf(),
        var daysOff: List<DayOff> = listOf()
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
        var entriesFiles: List<Path> = listOf(),
        var entriesConfig: EntriesConfig? = null,
        var configData: ConfigData? = null,
        var reportFiles: Map<ReportType, Path> = mapOf(),
        var reportEncoding: String = defaultEncoding,
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
        var reportEncoding: String = defaultEncoding,
        var reportTypes: List<ReportType> = listOf(),
        var fillAllMonthDays: Boolean = true
)

enum class AbsenceType {
    VACATION, UNJUSTIFIED, OTHER
}
enum class DayOffType {
    HOLIDAY, OTHER
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