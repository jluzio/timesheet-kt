package org.example.apps.timesheet.processors

import org.example.apps.timesheet.entries.Entry
import java.time.LocalDateTime

data class DayWorkData(
        var entries: List<Entry> = listOf(),
        var startDatetime: LocalDateTime? = null,
        var exitDatetime: LocalDateTime? = null,
        var workInMinutes: Long = 0,
        var breakInMinutes: Long = 0,
        var remarks: String? = null,
        var dayOff: Boolean = false,
        var absence: Boolean = false
)