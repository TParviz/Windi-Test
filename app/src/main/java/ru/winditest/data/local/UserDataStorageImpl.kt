package ru.winditest.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.winditest.data.remote.api.model.user.ProfileData
import ru.winditest.domain.local.UserDataStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataStorageImpl @Inject constructor(
    @ApplicationContext context: Context
): UserDataStorage {

    private companion object {
        const val USER_DATA_PREFS = "user_data_storage"
        val USER_DATA_KEY = stringPreferencesKey("user_data")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = USER_DATA_PREFS
    )
    private val dataStore = context.dataStore

    private val Preferences.userData: ProfileData?
        get() = this[USER_DATA_KEY]?.let {
            Json.Default.decodeFromString(it)
        }

    override val user: Flow<ProfileData?>
        get() = dataStore.data.map { it.userData }

    override suspend fun updateUserData(userData: ProfileData) {
        dataStore.edit { prefs ->
            prefs[USER_DATA_KEY] = Json.Default.encodeToString(userData)
        }
    }

    override suspend fun deleteUserData() {
        dataStore.edit { prefs ->
            prefs.remove(USER_DATA_KEY)
        }
    }
}