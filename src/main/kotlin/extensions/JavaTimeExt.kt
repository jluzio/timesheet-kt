package java.time.ext

import java.time.LocalDate

operator fun LocalDate.rangeTo(other: LocalDate) = LocalDateProgression(this, other)

class LocalDateProgression(override val start: LocalDate, override val endInclusive: LocalDate, val step: Long = 1): Iterable<LocalDate>, ClosedRange<LocalDate> {
    override fun iterator(): Iterator<LocalDate> {
        return LocalDateProgressionIterator(start, endInclusive, step)
    }
    infix fun step(days: Long) = LocalDateProgression(start, endInclusive, days)
}

internal class LocalDateProgressionIterator(start: LocalDate, val endInclusive: LocalDate, val step: Long) : Iterator<LocalDate> {
    var current = start
    override fun hasNext() = current <= endInclusive
    override fun next(): LocalDate {
        val next = current
        current = current.plusDays(step)
        return next
    }
}
