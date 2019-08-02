package org.example.apps.timesheet.appConfig

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [
    "org.example.apps.timesheet.config",
    "org.example.apps.timesheet.entries",
    "org.example.apps.timesheet.processors",
    "org.example.apps.timesheet.reports"
])
class CoreConfig
