package com.example.kalanacommerce.back.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Deklarasikan DataStore di level top-level agar hanya ada satu instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session_prefs"
)

class SessionManager(context: Context) {

    private val dataStore = context.dataStore

    suspend fun saveAuthData(
        token: String,
        isLoggedIn: Boolean
    ) {
        dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun clearAuthData() {
        dataStore.edit { it.clear() }
    }

    val tokenFlow: Flow<String?> = dataStore.data.map {
        it[KEY_TOKEN]
    }

    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map {
        it[KEY_IS_LOGGED_IN] ?: false
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }
}
