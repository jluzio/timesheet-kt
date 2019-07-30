package org.example.apps.timesheet.config

import org.example.apps.timesheet.AbstractTest
import org.junit.Test
import java.nio.file.Path
import javax.inject.Inject

class RunnerConfigReaderTest: AbstractTest() {
    @Inject
    lateinit var runnerConfigReader: RunnerConfigReader

    @Test
    fun test() {
        var resource = Path.of(javaClass.getResource("/runnerConfig-test.xml").toURI())
        var runnerConfig=  runnerConfigReader.read(resource)
        println("runnerConfig: $runnerConfig")
    }
}