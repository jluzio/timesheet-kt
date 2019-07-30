package org.example.apps.timesheet.test

import org.example.apps.timesheet.AbstractTest
import org.junit.Test
import javax.inject.Inject

class InfrastructureTest: AbstractTest() {
    @Inject
    lateinit var fooService: FooService

    @Test
    fun test() {
        log.debug("test debug")
        fooService.run()
    }

}