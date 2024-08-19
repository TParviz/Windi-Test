package ru.winditest.data.remote.api.model.user

import kotlinx.serialization.Serializable

@Serializable
data class EditUserInfoRequest(
    val name: String,
    val username: String,
    val birthday: String? = null,
    val city: String? = null,
    val vk: String? = null,
    val instagram: String? = null,
    val status: String? = null,
    val avatar: Avatar? = null
) {
    fun toProfileData(
        avatars: Avatars?,
        profileData: ProfileData
    ) = profileData.copy(
        birthday = birthday,
        city = city,
        vk = vk,
        status = status,
        instagram = instagram,
        avatars = avatars ?: profileData.avatars
    )
}


@Serializable
data class EditUserInfoResponse(
    val avatars: Avatars?
)