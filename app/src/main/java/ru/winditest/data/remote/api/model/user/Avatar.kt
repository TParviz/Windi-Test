package ru.winditest.data.remote.api.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Avatar(
    val filename: String,

    @SerialName("base_64")
    val base64: String
)