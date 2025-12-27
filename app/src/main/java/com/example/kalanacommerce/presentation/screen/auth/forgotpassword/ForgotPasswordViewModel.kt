package com.example.kalanacommerce.presentation.screen.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                isEmailValid = true // Reset error saat user mengetik
            )
        }
    }

    fun onSubmit() {
        val currentEmail = _uiState.value.email

        // 1. Validasi Format Email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
            _uiState.update { it.copy(isEmailValid = false) }
            return
        }

        // 2. Mulai Loading
        _uiState.update { it.copy(isLoading = true) }

        // 3. Simulasi API Call
        viewModelScope.launch {
            try {
                delay(2000) // Simulasi loading 2 detik
                _uiState.update {
                    it.copy(isLoading = false, isSuccess = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, isEmailValid = false)
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { ForgotPasswordUiState() }
    }
}