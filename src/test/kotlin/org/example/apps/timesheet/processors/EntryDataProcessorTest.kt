package org.example.apps.timesheet.processors

import org.example.apps.timesheet.AbstractTest
import org.example.apps.timesheet.config.ProcessConfig
import org.example.apps.timesheet.entries.Entry
import org.example.apps.timesheet.entries.EntryType
import org.example.apps.timesheet.entries.EntryTypeCode
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class EntryDataProcessorTest: AbstractTest() {
    @Inject
    lateinit var entryDataProcessor: EntryDataProcessor

    @Test
    fun test() {
        val anchorDate = LocalDate.of(2017, 7, 5)
        val enterTime1 = LocalTime.of(9, 0)
        val enterTime2 = LocalTime.of(10, 30)
        val enterTime3 = LocalTime.of(11, 0)
        val exitTime1 = LocalTime.of(18, 0)
        val exitTime2 = LocalTime.of(18, 30)
        val exitTime3 = LocalTime.of(19, 30)
        val breakEnterTime1 = LocalTime.of(12, 0)
        val breakEnterTime2 = LocalTime.of(13, 0)
        val breakExitTime1 = LocalTime.of(13, 0)
        val breakExitTime2 = LocalTime.of(13, 30)
        val serviceExit1 = LocalTime.of(14, 0)
        val serviceEnter1 = LocalTime.of(15, 30)

        fun _entry(date: LocalDate, time: LocalTime, type: EntryType, typeCode: EntryTypeCode = EntryTypeCode.valueOf(type.name), remarks: String? = null)
                = Entry(date, LocalDateTime.of(date, time), type, typeCode, remarks)

        val entries = listOf(
                // day1
                _entry(date = anchorDate.plusDays(0), time = enterTime1, type = EntryType.ENTER),
                _entry(date = anchorDate.plusDays(0), time = breakEnterTime1, type = EntryType.EXIT),
                _entry(date = anchorDate.plusDays(0), time = breakExitTime1, type = EntryType.ENTER),
                // day 2
                _entry(date = anchorDate.plusDays(1), time = enterTime2, type = EntryType.ENTER),
                _entry(date = anchorDate.plusDays(1), time = breakEnterTime2, type = EntryType.EXIT),
                _entry(date = anchorDate.plusDays(1), time = breakExitTime2, type = EntryType.ENTER),
                _entry(date = anchorDate.plusDays(1), time = exitTime2, type = EntryType.EXIT),
                // day 3
                _entry(date = anchorDate.plusDays(2), time = enterTime3, type = EntryType.ENTER),
                _entry(date = anchorDate.plusDays(2), time = breakEnterTime1, type = EntryType.EXIT),
                _entry(date = anchorDate.plusDays(2), time = breakExitTime2, type = EntryType.ENTER),
                _entry(date = anchorDate.plusDays(2), time = serviceExit1, type = EntryType.SERVICE_EXIT, typeCode = EntryTypeCode.EXIT),
                _entry(date = anchorDate.plusDays(2), time = serviceEnter1, type = EntryType.ENTER),
                _entry(date = anchorDate.plusDays(2), time = exitTime3, type = EntryType.EXIT),
                // day 4
                _entry(date = anchorDate.plusDays(3), time = enterTime3, type = EntryType.ENTER),
                _entry(date = anchorDate.plusDays(3), time = breakEnterTime1, type = EntryType.EXIT),
                _entry(date = anchorDate.plusDays(3), time = breakExitTime2, type = EntryType.ENTER),
                _entry(date = anchorDate.plusDays(3), time = serviceExit1, type = EntryType.SERVICE_EXIT, typeCode = EntryTypeCode.EXIT, remarks = "Service exit desc"),
                _entry(date = anchorDate.plusDays(3), time = serviceEnter1, type = EntryType.ENTER),
                _entry(date = anchorDate.plusDays(3), time = exitTime3, type = EntryType.EXIT),
                // day 1 again, to make sure list is unordered
                _entry(date = anchorDate.plusDays(0), time = exitTime1, type = EntryType.EXIT)
        )
        val config = ProcessConfig().apply {
            month = anchorDate.withDayOfMonth(1)
        }

        val result = entryDataProcessor.process(entries, config)
        println("result")
        result.forEach(::println)
    }
}