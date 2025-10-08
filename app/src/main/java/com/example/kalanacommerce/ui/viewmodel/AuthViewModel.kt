package com.example.kalanacommerce.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.data.AuthRepository
import com.example.kalanacommerce.data.local.SessionManager
import com.example.kalanacommerce.data.SignInRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Data class untuk merepresentasikan state UI pada layar login
data class SignInUiState(
    val isLoading: Boolean = false,
    val signInSuccess: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Fungsi untuk melakukan sign-in.
     * Ini akan dipanggil dari Composable LoginScreen.
     */
    fun signIn(signInRequest: SignInRequest) {
        // Mulai proses dengan menampilkan loading
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // 1. Panggil Repository untuk melakukan login ke API
                val response = authRepository.signIn(signInRequest)

                // 2. Jika berhasil, simpan token dan status login ke DataStore
                sessionManager.saveAuthData(
                    token = response.token,
                    isLoggedIn = true
                )

                // 3. Update UI state untuk menandakan login berhasil
                _uiState.update {
                    it.copy(isLoading = false, signInSuccess = true)
                }

            } catch (e: Exception) {
                // Jika terjadi error (dari Repository), update UI state dengan pesan error
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Terjadi kesalahan tidak diketahui")
                }
            }
        }
    }

    /**
     * Fungsi untuk mereset pesan error setelah ditampilkan.
     */
    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }
}
