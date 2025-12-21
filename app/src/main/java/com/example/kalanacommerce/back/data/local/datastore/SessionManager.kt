// In D:/My-Project/KalanaCommerce/app/src/main/java/com/example/kalanacommerce/back/data/local/datastore/SessionManager.kt

package com.example.kalanacommerce.back.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.kalanacommerce.back.data.remote.dto.auth.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session_prefs"
)

class SessionManager(context: Context) {

    private val dataStore = context.dataStore

    suspend fun saveAuthData(
        token: String,
        isLoggedIn: Boolean,
        user: UserDto
    ) {
        dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_IS_LOGGED_IN] = isLoggedIn
            it[KEY_USER_NAME] = user.name ?: ""
            it[KEY_USER_EMAIL] = user.email ?: ""
        }
    }

    // --- PERBAIKAN UTAMA DI SINI ---
    suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            // Jangan gunakan preferences.clear()
            // Hapus setiap key secara eksplisit untuk memastikan state benar-benar bersih
            preferences.remove(KEY_TOKEN)
            preferences.remove(KEY_IS_LOGGED_IN)
            preferences.remove(KEY_USER_NAME)
            preferences.remove(KEY_USER_EMAIL)
        }
    }

    val tokenFlow: Flow<String?> = dataStore.data.map {
        it[KEY_TOKEN]
    }

    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map {
        it[KEY_IS_LOGGED_IN] ?: false
    }

    val userFlow: Flow<UserDto?> = dataStore.data.map { preferences ->
        val name = preferences[KEY_USER_NAME]
        val email = preferences[KEY_USER_EMAIL]
        if (name != null && email != null) {
            UserDto(name = name, email = email)
        } else {
            null
        }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }
}
