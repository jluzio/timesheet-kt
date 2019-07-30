package org.example.apps.timesheet.entries

import org.example.apps.timesheet.AbstractTest
import org.example.apps.timesheet.config.EntriesConfigReader
import org.junit.Test
import java.nio.file.Path
import javax.inject.Inject

class EntryReaderTest: AbstractTest() {
    @Inject
    lateinit var entriesConfigReader: EntriesConfigReader
    @Inject
    lateinit var entryReader: EntryReader

    @Test
    fun test() {
        val entriesConfigFile = Path.of(javaClass.getResource("/entriesConfig-test.xml").toURI())
        val entriesConfig = entriesConfigReader.read(entriesConfigFile)

        val fileDefault = Path.of(javaClass.getResource("/201706.xls").toURI())
        val fileCustom = Path.of(javaClass.getResource("/201706_custom.xls").toURI())

        val entries = entryReader.read(fileDefault, entriesConfig)
        println("entries: $entries")

        val entriesCustom = entryReader.read(fileCustom, entriesConfig)
        println("entriesCustom: $entriesCustom")
    }
}