package com.example.kalanacommerce.data.local.datastore

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// Kita bisa gunakan datastore yang sama atau beda, disini kita buat khusus settings
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

class LanguageManager(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
    }

    // Ambil bahasa (Default ke Inggris 'en' jika belum diset)
    val languageFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_LANGUAGE] ?: "en"
    }

    // Simpan bahasa dan Terapkan langsung ke Aplikasi
    // LanguageManager.kt
    // LanguageManager.kt
    suspend fun setLanguage(code: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = code
        }
        // Paksa update locale di Main Thread agar responsif
        withContext(Dispatchers.Main) {
            val appLocale = LocaleListCompat.forLanguageTags(code)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }
}