package org.example.apps.timesheet.processors

import extensions.java.time.rangeTo
import org.example.apps.timesheet.config.ConfigPredicates
import org.example.apps.timesheet.config.ProcessConfig
import org.example.apps.timesheet.entries.Entry
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Named

@Named
class MonthDataProcessor {
    @Inject
    lateinit var entryDataProcessor: EntryDataProcessor

    fun process(entries: List<Entry>, config: ProcessConfig): List<DayWorkData> {
        val dayWorkDataList = entryDataProcessor.process(entries, config)

        val dateInMonth = config.month ?: dayWorkDataList.first().startDatetime!!.toLocalDate()
        val firstDayOfMonth = dateInMonth.withDayOfMonth(1)
        val lastDayOfMonth = dateInMonth.plusDays(1).withDayOfMonth(1).plusDays(-1)

        val monthDayWorkDataList = mutableListOf<DayWorkData>()
        for (date in firstDayOfMonth..lastDayOfMonth) {
            val dayWorkData = dayWorkDataList.find { it.startDatetime!!.toLocalDate() == date }
            if (dayWorkData != null) {
                monthDayWorkDataList += dayWorkData
            } else if (config.fillAllMonthDays) {
                val datetimeValue = date.atStartOfDay()
                val dayOff = isDayOff(date, config)
                val absence = isAbsence(date, config)

                val dayWorkData = DayWorkData(
                        startDatetime = datetimeValue,
                        exitDatetime = datetimeValue,
                        dayOff = dayOff,
                        absence = absence
                )
                monthDayWorkDataList.add(dayWorkData)
            }
        }
        return dayWorkDataList
    }

    private fun isDayOff(date: LocalDate, config: ProcessConfig): Boolean {
        return config.configData?.daysOff?.any(ConfigPredicates.dayOff(date, null)) ?: false
    }

    private fun isAbsence(date: LocalDate, config: ProcessConfig): Boolean {
        return config.configData?.absences?.any(ConfigPredicates.absence(date, null)) ?: false
    }

}