package extensions.java.util.date

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun Date.toLocalDateTime() = LocalDateTime.ofInstant(toInstant(), ZoneId.systemDefault())

fun Date.toInstant() = Instant.ofEpochMilli(time)

fun LocalDateTime.toDate() = Date.from(atZone(ZoneId.systemDefault()).toInstant())
