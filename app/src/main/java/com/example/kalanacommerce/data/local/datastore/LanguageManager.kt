package com.example.kalanacommerce.data.local.datastore

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        val KEY_SHOULD_SHOW_TOAST = booleanPreferencesKey("should_show_lang_toast") // Tambahkan ini
    }

    // Ambil bahasa (Default ke Inggris 'en' jika belum diset)
    val languageFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_LANGUAGE] ?: "en"
    }
    // Flow untuk memantau apakah ada toast yang perlu ditampilkan
    val shouldShowToastFlow: Flow<Boolean> = dataStore.data.map { it[KEY_SHOULD_SHOW_TOAST] ?: false }

    suspend fun clearPendingToast() {
        dataStore.edit { it[KEY_SHOULD_SHOW_TOAST] = false }
    }
    // Simpan bahasa dan Terapkan langsung ke Aplikasi
    // LanguageManager.kt
    // LanguageManager.kt
    // LanguageManager.kt
    // LanguageManager.kt
    suspend fun setLanguage(code: String) {
        // 1. Simpan ke DataStore
        dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = code
            preferences[KEY_SHOULD_SHOW_TOAST] = true
        }

        // 2. Beri jeda 100ms agar penulisan file ke disk selesai sepenuhnya
        // dan mencegah "balapan" antara simpan data vs restart activity
        kotlinx.coroutines.delay(100)

        // 3. Restart activity
        withContext(Dispatchers.Main) {
            val appLocale = LocaleListCompat.forLanguageTags(code)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }
}