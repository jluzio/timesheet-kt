package org.example.apps.timesheet.config

import java.time.LocalDate
import java.util.function.Predicate

object ConfigPredicates {

    fun dayOff(date: LocalDate, type: DayOffType? = null): (DayOff) -> Boolean {
        return { v -> (type == null || v.type == type) && v.date == date }
    }

    fun absence(date: LocalDate, type: DayOffType? = null): (Absence) -> Boolean {
        return { v ->
            Predicate<Absence> { (type == null || v.type == type) }
                    .and {
                        val startDate = it.date
                        val endDate = it.endDate
                        when {
                            startDate != null && endDate != null -> date in startDate..endDate
                            startDate != null && endDate == null -> date == startDate
                            else -> false
                        }
                    }
                    .test(v)
        }
    }
}
