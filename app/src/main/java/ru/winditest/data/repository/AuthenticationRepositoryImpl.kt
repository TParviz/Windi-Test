package ru.winditest.data.repository

import kotlinx.coroutines.flow.Flow
import ru.winditest.core.Resource
import ru.winditest.core.ResponseHandler
import ru.winditest.data.local.auth_state.AuthState
import ru.winditest.data.local.auth_state.toAuthState
import ru.winditest.data.remote.api.AuthenticationApi
import ru.winditest.data.remote.api.model.AuthResultDto
import ru.winditest.data.remote.api.model.RefreshTokenRequest
import ru.winditest.data.remote.api.model.UserAuthCode
import ru.winditest.data.remote.api.model.UserPhone
import ru.winditest.data.remote.api.model.UserRegistrationRequest
import ru.winditest.domain.local.AuthStateStorage
import ru.winditest.domain.local.UserDataStorage
import ru.winditest.domain.repository.AuthenticationRepository
import ru.winditest.domain.use_case.UpdateUserUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepositoryImpl @Inject constructor(
    private val authStateStorage: AuthStateStorage,
    private val userDataStorage: UserDataStorage,
    private val handler: ResponseHandler,
    private val authApi: AuthenticationApi,
    private val updateUserData: UpdateUserUseCase
) : AuthenticationRepository {

    override val authState: Flow<AuthState?> = authStateStorage.authState

    override suspend fun registerUser(request: UserRegistrationRequest) = handler {
        authApi.registerUser(request)
    }.also { resource ->
        resource.handle(
            onSuccess = {
                authStateStorage.updateAuthState(it.toAuthState())
                updateUserData()
            }
        )
    }

    override suspend fun requestAuthCode(phone: UserPhone) = handler {
        authApi.sendAuthenticationCode(phone)
    }

    override suspend fun checkAuthCode(authCode: UserAuthCode): Resource<AuthResultDto> = handler {
        authApi.checkAuthCode(authCode)
    }.also { resource ->
        resource.handle(
            onSuccess = {
                if (it.doesUserExist) {
                    authStateStorage.updateAuthState(it.toAuthState())
                    updateUserData()
                }
            }
        )
    }

    override suspend fun requestTokenRefreshment(refreshToken: RefreshTokenRequest) = handler {
        authApi.refreshAccessToken(refreshToken)
    }.also { resource ->
        resource.handle(
            onSuccess = {
                authStateStorage.updateAuthState(it.toAuthState())
            }
        )
    }

    override suspend fun checkAuthentication() = handler {
        authApi.checkAuthentication()
    }

    override suspend fun endSession() {
        authStateStorage.deleteAuthState()
        userDataStorage.deleteUserData()
    }
}