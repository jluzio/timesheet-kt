package org.example.apps.timesheet.entries

import java.time.LocalDate
import java.time.LocalDateTime

data class Entry(
        var date: LocalDate? = null,
        var datetime: LocalDateTime? = null,
        var type: EntryType? = null,
        var typeCode: EntryTypeCode? = null,
        var remarks: String? = null
)

enum class EntryType {
    ENTER,
    EXIT,
    SERVICE_EXIT,
    HOLLIDAY,
    VACATION,
    SICK_DAY
}
enum class EntryTypeCode {
    ENTER,
    EXIT,
    CUSTOM
}
