package ru.winditest.domain.local

import kotlinx.coroutines.flow.Flow
import ru.winditest.data.remote.api.model.user.ProfileData

interface UserDataStorage {
    val user: Flow<ProfileData?>
    suspend fun updateUserData(userData: ProfileData)
    suspend fun deleteUserData()
}