// Data/TokenManager.kt

package com.example.kalanacommerce.data

import android.content.Context
import android.content.SharedPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// KoinComponent diperlukan untuk injeksi konteks di luar Composable/ViewModel
class TokenManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_JWT_TOKEN = "jwt_token"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
    }

    fun getToken(): String? {
        // Ambil token JWT. Null jika belum ada.
        return prefs.getString(KEY_JWT_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_JWT_TOKEN).apply()
    }
}