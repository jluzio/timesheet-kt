package org.example.apps.timesheet.test

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.apps.timesheet.AbstractTest
import org.junit.Test
import java.io.StringWriter
import javax.inject.Inject

class JaxRsTest: AbstractTest() {
    @Inject
    lateinit var objectMapper: ObjectMapper

    @Test
    fun test() {
        var person = Person("John", "Doe")
        var output = StringWriter()
        objectMapper.writeValue(output, person)
        println("result: $output")
    }

}