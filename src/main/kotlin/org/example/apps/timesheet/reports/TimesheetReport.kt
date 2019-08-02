package org.example.apps.timesheet.reports

import org.example.apps.timesheet.processors.DayWorkData

interface TimesheetReport<CFG> {

    fun create(dayWorkDataList: List<DayWorkData>, config: CFG)

}