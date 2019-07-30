package org.example.apps.timesheet.config

import java.nio.file.Path
import javax.inject.Named
import javax.xml.bind.JAXBContext

@Named
class EntriesConfigReader {

    fun read(file: Path): EntriesConfig {
        var unmarshaller = JAXBContext.newInstance(EntriesConfig::class.java).createUnmarshaller()
         return unmarshaller.unmarshal(file.toFile()) as EntriesConfig
    }

}