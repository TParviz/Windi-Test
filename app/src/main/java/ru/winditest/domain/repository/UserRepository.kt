package ru.winditest.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.winditest.core.Resource
import ru.winditest.data.remote.api.model.user.EditUserInfoRequest
import ru.winditest.data.remote.api.model.user.EditUserInfoResponse
import ru.winditest.data.remote.api.model.user.ProfileData

interface UserRepository {
    fun getUserData(): Flow<ProfileData?>
    suspend fun editUserData(data: EditUserInfoRequest): Resource<EditUserInfoResponse>
    suspend fun updateUserData(): Resource<ProfileData>
}