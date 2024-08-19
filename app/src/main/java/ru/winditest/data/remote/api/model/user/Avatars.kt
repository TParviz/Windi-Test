package ru.winditest.data.remote.api.model.user

import kotlinx.serialization.Serializable

@Serializable
data class Avatars(
    val avatar: String,
    val bigAvatar: String,
    val miniAvatar: String
)