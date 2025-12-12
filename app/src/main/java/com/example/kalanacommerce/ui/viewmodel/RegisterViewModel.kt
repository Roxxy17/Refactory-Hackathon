package com.example.kalanacommerce.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.kalanacommerce.data.AuthService // Ganti dengan Repository jika Anda punya
import com.example.kalanacommerce.data.RegisterRequest
import com.example.kalanacommerce.data.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // <-- PERBAIKAN 1: Tambahkan import 'update'
import kotlinx.coroutines.launch

private const val TAG = "RegisterViewModel"

// Pastikan Anda meng-inject Repository, bukan Service secara langsung
class RegisterViewModel(private val authService: AuthService) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow() // <-- PERBAIKAN 2: Gunakan asStateFlow()

    fun register(name: String, email: String, password: String, phoneNumber: String) {
        // PERBAIKAN 3: Gunakan .update agar lebih aman dan ringkas
        _uiState.update { it.copy(isLoading = true, error = null) }

        val request = RegisterRequest(
            name = name,
            email = email,
            password = password,
            phoneNumber = phoneNumber
        )

        viewModelScope.launch {
            // Sebaiknya, panggil repository di sini
            val result = authService.register(request)

            result.onSuccess { response ->
                Log.i(TAG, "✅ [REGISTER SUCCESS] Pengguna baru terdaftar: Email: ${response.data?.email}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRegistered = true,
                        successMessage = response.message
                    )
                }
            }.onFailure { exception ->
                Log.e(TAG, "❌ [REGISTER FAILED] Gagal mendaftarkan email $email. Error: ${exception.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Terjadi kesalahan registrasi"
                    )
                }
            }
        }
    }

    /**
     * Mengembalikan state ke kondisi awal.
     * Dipanggil setelah navigasi atau setelah pesan error ditampilkan.
     */
    fun resetState() {
        _uiState.value = RegisterUiState()
    }
}
