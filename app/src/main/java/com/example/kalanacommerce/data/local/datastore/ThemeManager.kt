package com.example.kalanacommerce.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Enum untuk merepresentasikan pilihan tema
enum class ThemeSetting {
    LIGHT, DARK, SYSTEM
}

// Buat instance DataStore
private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

class ThemeManager(private val context: Context) {

    companion object {
        // Key untuk menyimpan pilihan tema di DataStore
        private val THEME_SETTING_KEY = stringPreferencesKey("theme_setting")
    }

    // --- PERBAIKAN UTAMA: Ubah Flow menjadi StateFlow ---
    val themeSettingFlow: StateFlow<ThemeSetting> = context.themeDataStore.data
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
        .stateIn(
            // StateFlow memerlukan CoroutineScope untuk dijalankan
            scope = CoroutineScope(Dispatchers.IO),
            // Mulai segera dan tetap aktif selama ada yang mengoleksi
            started = SharingStarted.WhileSubscribed(5_000),
            // Nilai awal yang akan ditampilkan sebelum DataStore selesai membaca
            initialValue = ThemeSetting.SYSTEM
        )

    // Fungsi untuk menyimpan pilihan tema baru dari pengguna
    suspend fun saveThemeSetting(themeSetting: ThemeSetting) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_SETTING_KEY] = themeSetting.name
        }
    }
}
