package org.example.apps.timesheet.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import javax.inject.Named


@Named
class ConfigDataReader {
    @Inject
    lateinit var objectMapper: ObjectMapper

    fun read(configDataDir: Path): ConfigData {
        var absencesMap = readFile(configDataDir.resolve("absences.json"), JsonTypeReferences.ABSENCES)
        var daysOffMap = readFile(configDataDir.resolve("daysOff.json"), JsonTypeReferences.DAYS_OFF)

        val configData = ConfigData()
        configData.absences = absencesMap?.flatMap {
            (type, values) -> values.map { it.apply { this.type = type } }
        }.orEmpty()
        configData.daysOff = daysOffMap?.flatMap {
            (type, values) -> values.map { it.apply { this.type = type } }
        }.orEmpty()

        return configData
    }

    internal fun <T> readFile(file: Path, typeRef: TypeReference<T>): T? {
        var value: T? = null
        if (Files.exists(file)) {
            value = objectMapper.readValue(file.toFile(), typeRef)
        }
        return value
    }

}