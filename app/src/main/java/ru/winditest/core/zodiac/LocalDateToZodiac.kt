package ru.winditest.core.zodiac

import androidx.annotation.StringRes
import ru.winditest.R
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

val zeroYearFormatter = DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd")
    .parseDefaulting(ChronoField.YEAR_OF_ERA, 1)
    .toFormatter()

private fun dayMonth(string: String) =
    LocalDate.parse(string, zeroYearFormatter)

@get:StringRes
val LocalDate.zodiacSignResource: Int
    get() = when (this) {
        in dayMonth("0001-03-21")..dayMonth("0001-04-20") -> R.string.aries
        in dayMonth("0001-04-21")..dayMonth("0001-05-21") -> R.string.taurus
        in dayMonth("0001-05-22")..dayMonth("0001-06-21") -> R.string.gemini
        in dayMonth("0001-06-22")..dayMonth("0001-07-23") -> R.string.cancer
        in dayMonth("0001-07-24")..dayMonth("0001-08-23") -> R.string.leo
        in dayMonth("0001-08-24")..dayMonth("0001-09-23") -> R.string.virgo
        in dayMonth("0001-09-24")..dayMonth("0001-10-22") -> R.string.libra
        in dayMonth("0001-10-24")..dayMonth("0001-11-21") -> R.string.scorpio
        in dayMonth("0001-11-23")..dayMonth("0001-12-21") -> R.string.sagittarius
        in dayMonth("0001-01-21")..dayMonth("0001-02-19") -> R.string.auqarius
        in dayMonth("0001-02-20")..dayMonth("0001-03-20") -> R.string.pisces
        else -> R.string.capricorn
    }