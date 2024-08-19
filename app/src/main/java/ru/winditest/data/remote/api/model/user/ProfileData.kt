package ru.winditest.data.remote.api.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileData(
    val name: String,
    val username: String,
    val birthday: String?,
    val city: String?,
    val vk: String?,
    val instagram: String?,
    val status: String?,
    val avatar: String?,
    val id: Int,
    val last: String?,
    val online: Boolean,
    val created: String,
    val phone: String,

    @SerialName("completed_task")
    val completedTask: Int,

    val avatars: Avatars?
)