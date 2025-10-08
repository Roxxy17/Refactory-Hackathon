package com.example.kalanacommerce.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.kalanacommerce.data.AuthService
import com.example.kalanacommerce.data.RegisterRequest
import com.example.kalanacommerce.data.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "RegisterViewModel"

class RegisterViewModel(private val authService: AuthService) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(fullName: String, email: String, password: String, phoneNumber: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, isRegistered = false)

        val request = RegisterRequest(
            full_name = fullName,
            email = email,
            password = password,
            phone_number = phoneNumber,
            role = "buyer"
        )

        viewModelScope.launch {
            val result = authService.register(request)

            result.onSuccess { response ->
                Log.i(TAG, "✅ [REGISTER SUCCESS] Pengguna baru terdaftar: Email: ${response.user.email}")

                _uiState.value = RegisterUiState(
                    isLoading = false,
                    isRegistered = true,
                    successMessage = response.message
                )
            }.onFailure { exception ->
                Log.e(TAG, "❌ [REGISTER FAILED] Gagal mendaftarkan email $email. Error: ${exception.message}")

                _uiState.value = RegisterUiState(
                    isLoading = false,
                    isRegistered = false,
                    error = exception.message ?: "Terjadi kesalahan registrasi yang tidak diketahui"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState()
    }
}