package com.example.kalanacommerce.presentation.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun register(
        name: String,
        email: String,
        password: String,
        phone: String
    ) {
        _uiState.update { it.copy(isLoading = true, message = null) }

        viewModelScope.launch {
            registerUseCase(name, email, password, phone)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true,
                            message = "Registrasi berhasil 🎉",
                            isError = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = e.message ?: "Registrasi gagal",
                            isError = true
                        )
                    }
                }
        }
    }

    fun resetMessage() {
        _uiState.update { it.copy(message = null, isError = false) }
    }
}

