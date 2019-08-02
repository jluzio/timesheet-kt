package org.example.apps.timesheet.processors

import org.apache.logging.log4j.LogManager
import org.example.apps.timesheet.config.ProcessConfig
import org.example.apps.timesheet.entries.Entry
import org.example.apps.timesheet.entries.EntryType
import java.time.LocalDate
import javax.inject.Named
import java.time.temporal.ChronoUnit

@Named
class EntryDataProcessor {
    private val log = LogManager.getLogger(javaClass)

    fun process(entries: List<Entry>, config: ProcessConfig): List<DayWorkData> {
        var lastDay: LocalDate? = null
        var currentDayWorkData: DayWorkData? = null
        val dayWorkDataList = mutableListOf<DayWorkData>()

        entries.sortedBy { it.datetime }.forEachIndexed { index, entry ->
            log.debug("Processing [{}]: {}", index, entry)

            // day transition
            if (lastDay == null || entry.date != lastDay) {
                currentDayWorkData?.run { dayWorkDataList += this }
                currentDayWorkData = DayWorkData()
            }

            val dayWorkData = currentDayWorkData!!
            var lastEntry = dayWorkData.entries.lastOrNull()
            lastDay = entry.date
            dayWorkData.entries += entry

            if (entry.type == EntryType.ENTER) {
                if (dayWorkData.startDatetime == null) {
                    dayWorkData.startDatetime = entry.datetime
                } else if (lastEntry != null) {
                    if (lastEntry.type == EntryType.SERVICE_EXIT) {
                        val currentBreakInMinutes = getDateDiffInMinutes(lastEntry, entry)
                        dayWorkData.remarks = getRemarksText(dayWorkData, lastEntry, currentBreakInMinutes)
                    } else if (lastEntry.type == EntryType.EXIT) {
                        val currentBreakInMinutes = getDateDiffInMinutes(lastEntry, entry)
                        dayWorkData.breakInMinutes += currentBreakInMinutes
                    }
                }
            } else if (entry.type == EntryType.EXIT) {
                dayWorkData.exitDatetime = entry.datetime
                dayWorkData.workInMinutes = ChronoUnit.MINUTES.between(dayWorkData.startDatetime, dayWorkData.exitDatetime) - dayWorkData.breakInMinutes
            } else if (entry.type in listOf(EntryType.DAY_OFF, EntryType.ABSENCE)) {
                val startOfDay = entry.date?.atStartOfDay()
                dayWorkData.apply {
                    dayOff = true
                    workInMinutes = 0
                    breakInMinutes = 0
                    remarks = entry.type?.name
                    startDatetime = startOfDay
                    exitDatetime = startOfDay
                }
            }

            if (index == entries.lastIndex) {
                dayWorkDataList += dayWorkData
            }
        }

        return dayWorkDataList
    }

    private fun getDateDiffInMinutes(firstEntry: Entry, secondEntry: Entry) =
            ChronoUnit.MINUTES.between(firstEntry.datetime, secondEntry.datetime)

    private fun getRemarksText(dayWorkData: DayWorkData, lastEntry: Entry, currentBreakInMinutes: Long): String {
        return "${lastEntry.type}($currentBreakInMinutes|${lastEntry.remarks})"
    }
}