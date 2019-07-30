package org.example.apps.timesheet.config

import org.example.apps.timesheet.AbstractTest
import org.junit.Test
import java.nio.file.Path
import javax.inject.Inject

class ConfigDataReaderTest: AbstractTest() {
    @Inject
    lateinit var configDataReader: ConfigDataReader

    @Test
    fun test() {
        val resource = javaClass.getResource("/configData-test").toURI()
        var configData = configDataReader.read(Path.of(resource))
        println("configData: $configData")
    }
}