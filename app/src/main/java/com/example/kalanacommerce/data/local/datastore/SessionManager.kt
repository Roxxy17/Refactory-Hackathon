package com.example.kalanacommerce.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Delegasi DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session_prefs"
)

class SessionManager(context: Context) {

    private val dataStore = context.dataStore

    // Konfigurasi JSON
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    // --- FUNGSI UTAMA YANG DIPANGGIL REPOSITORY ---
    suspend fun saveSession(token: String, user: ProfileUserDto) {
        // Kita panggil saveAuthData yang di bawah
        saveAuthData(token, true, user)
    }
    // ----------------------------------------------

    // Fungsi internal untuk simpan data lengkap
    suspend fun saveAuthData(
        token: String,
        isLoggedIn: Boolean,
        user: ProfileUserDto
    ) {
        val userJson = json.encodeToString(user)

        dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            preferences[KEY_IS_LOGGED_IN] = isLoggedIn
            preferences[KEY_USER_DATA] = userJson
        }
    }

    suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
            preferences[KEY_IS_LOGGED_IN] = false
            preferences.remove(KEY_USER_DATA)
        }
    }

    val tokenFlow: Flow<String?> = dataStore.data.map {
        it[KEY_TOKEN]
    }

    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map {
        it[KEY_IS_LOGGED_IN] ?: false
    }

    val userFlow: Flow<ProfileUserDto?> = dataStore.data.map { preferences ->
        val userString = preferences[KEY_USER_DATA]
        if (!userString.isNullOrEmpty()) {
            try {
                json.decodeFromString<ProfileUserDto>(userString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    // Last Email
    suspend fun saveLastEmail(email: String) {
        dataStore.edit { it[KEY_LAST_EMAIL] = email }
    }
    val lastEmail: Flow<String?> = dataStore.data.map { it[KEY_LAST_EMAIL] }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_DATA = stringPreferencesKey("user_data_json")
        private val KEY_LAST_EMAIL = stringPreferencesKey("last_email")
    }
}