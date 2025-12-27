package com.example.kalanacommerce.data.local.datastore // atau sesuaikan dengan path Anda

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Enum untuk merepresentasikan pilihan tema
enum class ThemeSetting {
    LIGHT, DARK, SYSTEM
}

// Buat instance DataStore sama seperti SessionManager
private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

class ThemeManager(private val context: Context) {

    companion object {
        // Key untuk menyimpan pilihan tema di DataStore
        private val THEME_SETTING_KEY = stringPreferencesKey("theme_setting")
    }

    // Flow untuk mendapatkan pilihan tema saat ini. Default-nya adalah SYSTEM.
    val themeSettingFlow: Flow<ThemeSetting> = context.themeDataStore.data
        .map { preferences ->
            // Baca string dari DataStore, jika tidak ada, gunakan "SYSTEM"
            val themeName = preferences[THEME_SETTING_KEY] ?: ThemeSetting.SYSTEM.name
            // Ubah string kembali menjadi Enum
            try {
                ThemeSetting.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                // Jika ada nilai yang tidak valid, kembali ke default
                ThemeSetting.SYSTEM
            }
        }

    // Fungsi untuk menyimpan pilihan tema baru dari pengguna
    suspend fun saveThemeSetting(themeSetting: ThemeSetting) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_SETTING_KEY] = themeSetting.name
        }
    }
}