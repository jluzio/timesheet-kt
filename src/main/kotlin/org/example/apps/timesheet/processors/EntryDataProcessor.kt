package org.example.apps.timesheet.processors

import org.apache.logging.log4j.LogManager
import org.example.apps.timesheet.config.ProcessConfig
import org.example.apps.timesheet.entries.Entry
import java.time.LocalDate
import javax.inject.Named

@Named
class EntryDataProcessor {
    val log = LogManager.getLogger(javaClass)

    fun process(entries: List<Entry>, config: ProcessConfig): List<DayWorkData> {
        var lastDay: LocalDate? = null
        var dayWorkData: DayWorkData? = null
        val dayWorkDatas = mutableListOf<DayWorkData>()

        entries.sortedBy { it.datetime }.forEachIndexed { index, entry ->
            // day transition
            if (lastDay == null || entry.date != lastDay) {
                dayWorkData?.run { dayWorkDatas += this }
            }
            lastDay = entry.date

            log.debug("Processing [{}]: {}", index, entry)
        }

        return dayWorkDatas
    }
}