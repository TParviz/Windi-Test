package ru.winditest.data.repository

import kotlinx.coroutines.flow.first
import ru.winditest.core.ResponseHandler
import ru.winditest.data.remote.api.UserApi
import ru.winditest.data.remote.api.model.user.EditUserInfoRequest
import ru.winditest.domain.local.UserDataStorage
import ru.winditest.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userStorage: UserDataStorage,
    private val handler: ResponseHandler
) : UserRepository {
    override fun getUserData() =
        userStorage.user

    override suspend fun editUserData(data: EditUserInfoRequest) = handler {
        userApi.changeUserInfo(data)
    }.also {
        it.handle(
            onSuccess = {
                val currentUserData = userStorage.user.first() ?: return@handle
                userStorage.updateUserData(
                    userData = data.toProfileData(it.avatars, currentUserData)
                )
            }
        )
    }

    override suspend fun updateUserData() = handler {
        userApi.getCurrentUser().profileData
    }.also { resource ->
        resource.handle(
            onSuccess = {
                userStorage.updateUserData(it)
            }
        )
    }
}