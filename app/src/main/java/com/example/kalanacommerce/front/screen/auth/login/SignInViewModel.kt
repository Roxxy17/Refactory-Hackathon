package com.example.kalanacommerce.front.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.back.data.local.datastore.SessionManager
import com.example.kalanacommerce.back.domain.usecase.auth.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val sessionManager: SessionManager // Tetap butuh ini untuk simpan token
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    // UBAH DI SINI: Parameter langsung String (seperti RegisterViewModel)
    fun signIn(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            signInUseCase(email, password)
                .onSuccess { token ->
                    // Simpan sesi (ini bedanya Login dan Register)
                    sessionManager.saveAuthData(token, true)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
        }
    }

    // Opsional: Reset state jika perlu
    fun resetState() {
        _uiState.update { SignInUiState() }
    }
}