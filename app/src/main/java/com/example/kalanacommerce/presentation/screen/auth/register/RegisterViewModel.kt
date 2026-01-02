package com.example.kalanacommerce.presentation.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource // Pastikan import Resource
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
        viewModelScope.launch {
            // Panggil UseCase lalu .collect hasilnya
            registerUseCase(name, email, password, phone).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, message = null) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRegistered = true,
                                message = result.data ?: "Registrasi berhasil 🎉",
                                isError = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                message = result.message ?: "Registrasi gagal",
                                isError = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun resetMessage() {
        _uiState.update { it.copy(message = null, isError = false) }
    }
}