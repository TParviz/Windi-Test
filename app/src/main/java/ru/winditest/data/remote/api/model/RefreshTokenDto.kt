package ru.winditest.data.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenDto(

    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String
)