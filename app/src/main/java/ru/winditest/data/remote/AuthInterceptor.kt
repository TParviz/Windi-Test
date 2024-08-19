package ru.winditest.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import ru.winditest.domain.local.AuthStateStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authStateStorage: AuthStateStorage,
    private val tokenService: TokenRefreshmentService
): Interceptor {

    private val authenticationPaths = setOf(
        "/api/v1/users/register/",
        "/api/v1/users/send-auth-code/",
        "/api/v1/users/check-auth-code/"
    )

    override fun intercept(chain: Interceptor.Chain) =
        if (chain.request().url.encodedPath in authenticationPaths) {
            chain.proceed(chain.request())
        } else {
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer ${getAccessToken()}")
                    .build()
            )
        }

    private fun getAccessToken() = runBlocking {
        tokenService.withFreshTokens(authStateStorage) { accessToken, exception ->
            exception?.let {
                throw it
            }

            return@runBlocking accessToken
        }
    }
}