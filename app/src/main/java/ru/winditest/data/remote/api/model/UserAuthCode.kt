package ru.winditest.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthCode(
    val code: String,
    val phone: String
)