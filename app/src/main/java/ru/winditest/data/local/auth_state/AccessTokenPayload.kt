package ru.winditest.data.local.auth_state

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenPayload(
    val sub: Int,
    val username: String,
    val phone: String,
    val iat: Int,
    val exp: Int
) {
    val hasTokenExpired: Boolean =
        exp < System.currentTimeMillis() / 1000
}