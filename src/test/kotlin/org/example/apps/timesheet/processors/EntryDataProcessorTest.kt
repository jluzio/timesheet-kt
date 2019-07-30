package org.example.apps.timesheet.processors

import org.example.apps.timesheet.AbstractTest
import org.example.apps.timesheet.config.ProcessConfig
import org.example.apps.timesheet.entries.Entry
import org.example.apps.timesheet.entries.EntryType
import org.example.apps.timesheet.entries.EntryTypeCode
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class EntryDataProcessorTest: AbstractTest() {
    @Inject
    lateinit var entryDataProcessor: EntryDataProcessor

    @Test
    fun test() {
        val entryBase = Entry(date = LocalDate.now(), type = EntryType.ENTER, typeCode = EntryTypeCode.ENTER, datetime = LocalDateTime.now())
        val entries = (1..10).map {
            val date = LocalDate.now().plusDays(it.toLong())
            entryBase.copy(date = date, datetime = date.atStartOfDay())
        }
        val config = ProcessConfig()

        val result = entryDataProcessor.process(entries, config)
        println("result: $result")
    }
}