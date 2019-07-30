package org.example.apps.timesheet.entries

import com.google.common.base.CharMatcher
import com.google.common.base.Splitter
import org.example.apps.timesheet.config.EntriesConfig
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Named


@Named
class EntryReader {

    fun read(file: Path, entriesConfig: EntriesConfig): List<Entry> {
        var lineIndex = -1
        val dateFormatter = DateTimeFormatter.ofPattern(entriesConfig.dateFormat)
        val datetimeFormatter = DateTimeFormatter.ofPattern(entriesConfig.dateTimeFormat)
        val splitter = Splitter.on(CharMatcher.javaIsoControl())
        val entries = mutableListOf<Entry>()

        Files.readAllLines(file, charset(entriesConfig.encoding)).forEach { line ->
            lineIndex++
            // header
            if (lineIndex == 0 || line.startsWith("#")) {
                return@forEach
            }
            val tokens = splitter.splitToList(line)
            val (dateString, timeString, entryTypeString) = tokens
            val descString = tokens.getOrNull(3).orEmpty()

            val fileEntry = Entry(
                    date = LocalDate.parse(dateString, dateFormatter),
                    datetime = LocalDateTime.parse("$dateString $timeString", datetimeFormatter)
            )
            when(entryTypeString) {
                "E" -> {
                    fileEntry.type = EntryType.ENTER
                    fileEntry.typeCode = EntryTypeCode.ENTER
                }
                "S" -> {
                    fileEntry.type = EntryType.EXIT
                    if (descString == entriesConfig.serviceExitText) {
                        fileEntry.type = EntryType.SERVICE_EXIT
                    } else if (descString.isNotEmpty()) {
                        fileEntry.type = EntryType.SERVICE_EXIT
                        fileEntry.remarks = descString
                    } else {
                        fileEntry.type = EntryType.EXIT
                    }
                }
                "C" -> {
                    fileEntry.type = EntryType.valueOf(descString)
                    fileEntry.typeCode = EntryTypeCode.CUSTOM
                }
            }

            entries += fileEntry
        }

        return entries
    }
}