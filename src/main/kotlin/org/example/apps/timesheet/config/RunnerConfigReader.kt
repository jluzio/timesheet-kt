package org.example.apps.timesheet.config

import java.nio.file.Path
import javax.inject.Named
import javax.xml.bind.JAXBContext

@Named
class RunnerConfigReader {

    fun read(file: Path): RunnerConfig {
        var unmarshaller = JAXBContext.newInstance(RunnerConfig::class.java).createUnmarshaller()
        return unmarshaller.unmarshal(file.toFile()) as RunnerConfig
    }
}