package ru.winditest.core.extentions

import android.content.Context
import android.util.Log
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import ru.winditest.R
import java.time.DateTimeException
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun Instant.toLocalDateTime() = toLocalDateTime(TimeZone.currentSystemDefault())
fun String.fromIso8601ToInstant() =
    java.time.Instant.from(
        DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault()).parse(this)
    ).toKotlinInstant().toLocalDateTime()

/**
 * Parses ISO8601 string and returns a pair of Strings representing date and time respectively
 */
fun String.fromIso8601ToDateTime(
    context: Context,
    includeSeconds: Boolean = true
): Pair<String, String> = try {
    fromIso8601ToInstant().run {
        val day = dayOfMonth.toString().padStart(2, '0')
        val month = monthNumber.toString().padStart(2, '0')
        val hour = hour.toString().padStart(2, '0')
        val minute = minute.toString().padStart(2, '0')
        val second = second.toString().padStart(2, '0')

        "$day.$month.$year" to if (includeSeconds) "$hour:$minute:$second" else "$hour:$minute"
    }
} catch (e: DateTimeException) {
    e.printStackTrace()
    Log.e("DateTime", "Unable to parse $this")
    context.getString(R.string.time_parsing_error) to ""
}