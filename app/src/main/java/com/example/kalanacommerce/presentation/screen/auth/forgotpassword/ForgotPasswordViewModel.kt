package com.example.kalanacommerce.presentation.screen.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.usecase.auth.forgot.ForgotPasswordUseCase
import com.example.kalanacommerce.domain.usecase.auth.forgot.ResetPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        // Reset error dan status sukses saat user mengetik ulang
        _uiState.update {
            it.copy(email = newEmail, isEmailValid = true, error = null, isSuccess = false)
        }
    }

    // Fungsi untuk mereset error setelah Toast muncul
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // Fungsi untuk mereset success setelah navigasi (PENTING)
    fun clearSuccessState() {
        _uiState.update { it.copy(isSuccess = false, isResetSuccess = false) }
    }

    fun onSubmit() {
        val currentEmail = _uiState.value.email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
            _uiState.update { it.copy(isEmailValid = false, error = "Format email tidak valid") }
            return
        }

        viewModelScope.launch {
            forgotPasswordUseCase(currentEmail).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
                    is Resource.Success -> _uiState.update {
                        // HANYA set success jika result benar-benar Success
                        it.copy(isLoading = false, isSuccess = true)
                    }
                    is Resource.Error -> _uiState.update {
                        // Pastikan isSuccess FALSE jika error
                        it.copy(isLoading = false, isSuccess = false, error = result.message)
                    }
                }
            }
        }
    }

    // --- STEP 2 ---

    fun onOtpChange(otp: String) {
        _uiState.update { it.copy(otp = otp, error = null) }
    }

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password, error = null) }
    }

    fun onResetPassword(email: String) {
        val state = _uiState.value

        // Validasi OTP Minimal 6 Digit sebelum panggil API
        if (state.otp.length < 6) {
            _uiState.update { it.copy(error = "Kode OTP tidak valid") }
            return
        }

        if (state.newPassword.isEmpty()) {
            _uiState.update { it.copy(error = "Password baru wajib diisi") }
            return
        }

        viewModelScope.launch {
            resetPasswordUseCase(email, state.otp, state.newPassword).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
                    is Resource.Success -> _uiState.update { it.copy(isLoading = false, isResetSuccess = true) }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, isResetSuccess = false, error = result.message)
                    }
                }
            }
        }
    }
}