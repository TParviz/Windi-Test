package ru.winditest.data.remote.api.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(

    @SerialName("profile_data")
    val profileData: ProfileData
)