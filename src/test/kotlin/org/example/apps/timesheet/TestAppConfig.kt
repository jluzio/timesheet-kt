package org.example.apps.timesheet

import org.apache.logging.log4j.LogManager
import org.example.apps.timesheet.appConfig.CoreConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.event.EventListener

@Configuration
@EnableAutoConfiguration
@Import(CoreConfig::class)
class TestAppConfig {
    private val log = LogManager.getLogger(javaClass)

    @EventListener(ApplicationStartedEvent::class)
    fun logAppStart() {
        log.debug("Timesheet[TEST] started")
    }

}