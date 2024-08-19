package ru.winditest.presentation.screen.app.profile

import android.net.Uri
import java.time.LocalDate

sealed interface ProfileEvent {
    object OnUpdate: ProfileEvent
    object OnEdit: ProfileEvent
    object OnConfirmEdit: ProfileEvent
    object OnCancelEdit: ProfileEvent
    object OnSignOut: ProfileEvent

    data class OnCityChanged(val city: String): ProfileEvent
    data class OnVkChanged(val vk: String): ProfileEvent
    data class OnInstagramChanged(val inst: String): ProfileEvent
    data class OnStatusChanged(val status: String): ProfileEvent
    data class OnPhotoPicked(val uri: Uri?, val base64Factory: () -> String?): ProfileEvent
    data class OnBirthdayPicked(val date: LocalDate?): ProfileEvent
}