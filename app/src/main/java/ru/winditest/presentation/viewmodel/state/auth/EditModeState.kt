package ru.winditest.presentation.viewmodel.state.auth

import ru.winditest.data.remote.api.model.user.ProfileData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class EditModeState(
    val birthday: LocalDate?,
    val city: String?,
    val vk: String?,
    val instagram: String?,
    val status: String?
) {

    val formattedBirthday = birthday?.format(
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    )

    companion object {
        fun fromUserData(data: ProfileData?) =
            EditModeState(
                birthday = data?.birthday?.let { LocalDate.parse(it) },
                city = data?.city,
                vk = data?.vk,
                instagram = data?.instagram,
                status = data?.status
            )
    }
}
