package ru.winditest.data.local.auth_state

import android.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.winditest.data.remote.api.model.AuthResultDto
import ru.winditest.data.remote.api.model.RefreshTokenDto
import ru.winditest.data.remote.api.model.UserRegistrationDto

@Serializable
data class AuthState(
    val accessToken: String?,
    val refreshToken: String?,
    val isAuthorized: Boolean = false
) {
    companion object {
        val Empty = AuthState(
            accessToken = null,
            refreshToken = null,
            isAuthorized = false
        )
    }

    val accessTokenPayload: AccessTokenPayload? = accessToken?.let {
        Json.Default.decodeFromString(
            Base64.decode(
                accessToken.substringAfter('.').substringBefore('.'),
                Base64.DEFAULT
            ).decodeToString()
        )
    }

    val userId: Int? = accessTokenPayload?.sub
}

fun AuthResultDto.toAuthState() = AuthState(accessToken, refreshToken, true)

fun RefreshTokenDto.toAuthState() = AuthState(accessToken, refreshToken, true)

fun UserRegistrationDto.toAuthState() = AuthState(accessToken, refreshToken, isAuthorized = true)