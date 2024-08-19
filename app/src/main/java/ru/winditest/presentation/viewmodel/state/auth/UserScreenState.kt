package ru.winditest.presentation.viewmodel.state.auth

import android.net.Uri
import ru.winditest.core.UiText
import ru.winditest.core.zodiac.zeroYearFormatter
import ru.winditest.core.zodiac.zodiacSignResource
import ru.winditest.data.remote.api.model.user.ProfileData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class UserScreenState(
    val user: ProfileData? = null,
    val isUpdating: Boolean = false,
    val isEditing: Boolean = false,
    val pickedPhotoUri: Uri? = null,

    val firstFetchError: UiText? = null
) {

    val userBirthdayAsLocalDate = user?.birthday?.let {
        LocalDate.parse(it)
    }

    val formattedBirthday = userBirthdayAsLocalDate?.format(
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    )

    val userZodiacSign: UiText? =
        user?.birthday?.let {
            UiText.StringResource(
                resId = LocalDate.parse(
                    it.replaceBefore('-', "0001"),
                    zeroYearFormatter
                ).zodiacSignResource
            )
        }
}