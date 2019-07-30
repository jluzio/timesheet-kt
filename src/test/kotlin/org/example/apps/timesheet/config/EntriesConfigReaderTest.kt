package org.example.apps.timesheet.config

import org.example.apps.timesheet.AbstractTest
import org.junit.Test
import java.nio.file.Path
import javax.inject.Inject

class EntriesConfigReaderTest: AbstractTest() {
    @Inject
    lateinit var entriesConfigReader: EntriesConfigReader

    @Test
    fun test() {
        val resource = javaClass.getResource("/entriesConfig-test.xml").toURI()
        var entriesConfig = entriesConfigReader.read(Path.of(resource))
        println("entriesConfig: $entriesConfig")
    }
}