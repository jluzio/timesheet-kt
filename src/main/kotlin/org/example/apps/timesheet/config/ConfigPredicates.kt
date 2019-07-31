package org.example.apps.timesheet.config

import java.time.LocalDate

object ConfigPredicates {

    fun dayOff(date: LocalDate, type: DayOffType? = null): (DayOff) -> Boolean {
        return { v -> (type == null || v.type == type) && v.date == date }
    }

    fun absence(date: LocalDate, type: DayOffType? = null): (Absence) -> Boolean {
        return { v -> (type == null || v.type == type) && v.date == date }
    }

}