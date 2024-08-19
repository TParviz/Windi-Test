package ru.winditest.data.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequest(

    @SerialName("name")
    val name: String,

    @SerialName("phone")
    val phone: String,

    @SerialName("username")
    val userName: String
)