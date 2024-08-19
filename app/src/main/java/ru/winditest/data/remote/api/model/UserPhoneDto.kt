package ru.winditest.data.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPhoneDto(

    @SerialName("is_success")
    val isSuccess: Boolean
)