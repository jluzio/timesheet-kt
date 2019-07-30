package org.example.apps.timesheet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TimesheetApplication

fun main(args: Array<String>) {
	runApplication<TimesheetApplication>(*args)
}
