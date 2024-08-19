package ru.winditest.data.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResultDto(

    @SerialName("access_token")
    val accessToken: String?,

    @SerialName("is_user_exists")
    val doesUserExist: Boolean,

    @SerialName("refresh_token")
    val refreshToken: String?,

    @SerialName("user_id")
    val userId: Int?
)