package org.example.apps.timesheet

import org.example.apps.timesheet.appConfig.AppConfig
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootConfiguration
@Import(AppConfig::class)
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
