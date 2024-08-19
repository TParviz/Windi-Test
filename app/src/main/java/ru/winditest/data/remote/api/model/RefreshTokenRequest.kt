package ru.winditest.data.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(

    @SerialName("refresh_token")
    val refreshToken: String
)