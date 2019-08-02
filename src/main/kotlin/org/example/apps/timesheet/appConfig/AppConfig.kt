package org.example.apps.timesheet.appConfig

import org.apache.logging.log4j.LogManager
import org.example.apps.timesheet.processors.TimesheetRunner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.event.EventListener
import java.nio.file.Path
import javax.inject.Inject

@Configuration
@EnableAutoConfiguration
@Import(CoreConfig::class)
class AppConfig : CommandLineRunner {
    private val log = LogManager.getLogger(javaClass)
    @Inject
    lateinit var timesheetRunner: TimesheetRunner

    @EventListener(ApplicationStartedEvent::class)
    fun logAppStart() {
        log.debug("Timesheet[MAIN] started")
    }


    override fun run(vararg args: String?) {
        timesheetRunner.run {
            run(getRunnerConfig(Path.of("/home/timesheet")))
        }
    }
}