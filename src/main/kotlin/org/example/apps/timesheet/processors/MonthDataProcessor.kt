package org.example.apps.timesheet.processors

import org.example.apps.timesheet.config.ProcessConfig
import org.example.apps.timesheet.entries.Entry
import org.example.apps.timesheet.config.ConfigPredicates
import java.time.ext.*
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Named

@Named
class MonthDataProcessor {
    @Inject
    lateinit var entryDataProcessor: EntryDataProcessor

    fun process(entries: List<Entry>, config: ProcessConfig) {
        val dayWorkDatas = entryDataProcessor.process(entries, config)

        val dateInMonth = config.month ?: dayWorkDatas.first().startDatetime!!.toLocalDate()
        val firstDayOfMonth = dateInMonth.withDayOfMonth(1)
        val lastDayOfMonth = dateInMonth.plusDays(1).withDayOfMonth(1).plusDays(-1)

        val monthDayWorkDatas = mutableListOf<DayWorkData>()
        for (date in firstDayOfMonth..lastDayOfMonth) {
            val dayWorkData = dayWorkDatas.find { it.startDatetime!!.toLocalDate() == date }
            if (dayWorkData != null) {
                monthDayWorkDatas += dayWorkData
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
                monthDayWorkDatas.add(dayWorkData)
            }
        }
    }

    private fun isDayOff(date: LocalDate, config: ProcessConfig): Boolean {
        return config.configData?.daysOff?.any(ConfigPredicates.dayOff(date, null)) ?: false
    }

    private fun isAbsence(date: LocalDate, config: ProcessConfig): Boolean {
        return config.configData?.absences?.any(ConfigPredicates.absence(date, null)) ?: false
    }

}