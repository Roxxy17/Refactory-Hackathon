package com.example.kalanacommerce.data.local

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
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

class SessionManager(context: Context) {

    private val dataStore = context.dataStore

    /**
     * Menyimpan token dan status login ke DataStore.
     */
    suspend fun saveAuthData(token: String, isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            preferences[KEY_IS_LOGGED_IN] = isLoggedIn
        }
    }

    /**
     * Menghapus semua data sesi (untuk logout).
     */
    suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Flow untuk mendapatkan token.
     * Akan bernilai null jika tidak ada token.
     */
    val tokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_TOKEN]
    }

    /**
     * Flow untuk mendapatkan status login.
     * Akan bernilai false jika belum pernah login.
     */
    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_IS_LOGGED_IN] ?: false
    }

    // Kunci untuk menyimpan data di DataStore
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }
}
