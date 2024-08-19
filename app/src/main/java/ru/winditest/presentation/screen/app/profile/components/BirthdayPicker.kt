package ru.winditest.presentation.screen.app.profile.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.datepicker.MaterialDatePicker
import ru.winditest.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
fun rememberBirthdayPicker(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
): MaterialDatePicker<Long> {
    val context = LocalContext.current

    return remember {
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(context.getString(R.string.user_birthday))
            .setSelection(
                initialDate?.atStartOfDay()
                    ?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
                    ?: MaterialDatePicker.thisMonthInUtcMilliseconds()
            )
            .build()
            .apply {
                addOnPositiveButtonClickListener { selection ->
                    val localDate = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    onDateSelected(localDate)
                }
            }
    }
}