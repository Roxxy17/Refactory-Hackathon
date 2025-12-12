package com.example.kalanacommerce.ui.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.data.AuthService
import com.example.kalanacommerce.data.SignInRequest
import com.example.kalanacommerce.data.SignInUiState
import com.example.kalanacommerce.data.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(private val authService: AuthService,private val tokenManager: TokenManager) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

    fun signIn(email: String, password: String) {
        // 1. Reset error dan set loading
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        // Membuat request dari argumen
        val request = SignInRequest(email = email, password = password)

        viewModelScope.launch {
            // ... (Logika pemanggilan service tetap sama)
            val result = authService.signIn(request)

            result.onSuccess { response ->
                val logMessage = """
                    Pengguna berhasil login:
                    - Email: ${response.user.email}
                    - Nama Lengkap: ${response.user.name}
                    - ID Pengguna: ${response.user.id}
                    - Token (sebagian): ${response.token.take(15)}...
                """.trimIndent()
                tokenManager.saveToken(response.token)
                // Mencetak ke Logcat dengan level INFORMASI (I)
                Log.i(TAG, "✅ [SUCCESS] $logMessage")
                _uiState.value = SignInUiState(
                    isLoading = false,
                    isAuthenticated = true,
                    token = response.token
                )
            }.onFailure { exception ->
                _uiState.value = SignInUiState(
                    isLoading = false,
                    isAuthenticated = false,
                    error = exception.message ?: "Terjadi kesalahan yang tidak diketahui"
                )
            }
        }
    }
}