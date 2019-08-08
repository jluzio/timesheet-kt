package org.example.apps.timesheet.config

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ConfigPredicatesTest {

    @Test
    fun dayOff_test() {
        val date1 = LocalDate.of(2000, 1, 1)
        val date2 = LocalDate.of(2000, 1, 2)
        val date3 = LocalDate.of(2000, 1, 3)
        val date4 = LocalDate.of(2000, 1, 4)
        val date5 = LocalDate.of(2000, 1, 5)

        val dayOff_date1 = DayOff(date = date1)
        val dayOff_date3 = DayOff(date = date3)

        assertTrue(listOf(dayOff_date1).any( ConfigPredicates.dayOff(date1) ))
        assertFalse(listOf(dayOff_date1).any( ConfigPredicates.dayOff(date2) ))

        assertFalse(listOf(dayOff_date3).any( ConfigPredicates.dayOff(date1) ))
        assertFalse(listOf(dayOff_date3).any( ConfigPredicates.dayOff(date2) ))
        assertTrue(listOf(dayOff_date3).any( ConfigPredicates.dayOff(date3) ))
        assertFalse(listOf(dayOff_date3).any( ConfigPredicates.dayOff(date4) ))
        assertFalse(listOf(dayOff_date3).any( ConfigPredicates.dayOff(date5) ))
    }

    @Test
    fun absence_test() {
        val date1 = LocalDate.of(2000, 1, 1)
        val date2 = LocalDate.of(2000, 1, 2)
        val date3 = LocalDate.of(2000, 1, 3)
        val date4 = LocalDate.of(2000, 1, 4)
        val date5 = LocalDate.of(2000, 1, 5)

        val absence_date1 = Absence(date1)
        val absence_date2_date4 = Absence(date2, date4)

        assertTrue(listOf(absence_date1).any( ConfigPredicates.absence(date1) ))
        assertFalse(listOf(absence_date1).any( ConfigPredicates.absence(date2) ))

        assertTrue(listOf(absence_date2_date4).any( ConfigPredicates.absence(date2) ))
        assertTrue(listOf(absence_date2_date4).any( ConfigPredicates.absence(date3) ))
        assertTrue(listOf(absence_date2_date4).any( ConfigPredicates.absence(date4) ))
        assertFalse(listOf(absence_date2_date4).any( ConfigPredicates.absence(date1) ))
        assertFalse(listOf(absence_date2_date4).any( ConfigPredicates.absence(date5) ))

    }
}