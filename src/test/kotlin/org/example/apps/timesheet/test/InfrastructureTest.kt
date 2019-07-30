package org.example.apps.timesheet.test

import org.apache.logging.log4j.LogManager
import org.example.apps.timesheet.AbstractTest
import org.junit.Test
import javax.inject.Inject

class InfrastructureTest: AbstractTest() {
    val log = LogManager.getLogger(javaClass)
    @Inject
    lateinit var fooService: FooService

    @Test
    fun test() {
        log.debug("test debug")
        fooService.run()
    }

}