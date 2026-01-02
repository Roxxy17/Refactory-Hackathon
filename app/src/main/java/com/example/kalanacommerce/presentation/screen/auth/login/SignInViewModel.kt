package com.example.kalanacommerce.presentation.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource // Pastikan import Resource
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.domain.usecase.auth.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    val lastEmail: StateFlow<String?> = sessionManager.lastEmail
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun signIn(email: String, password: String, isRemembered: Boolean) {
        viewModelScope.launch {
            signInUseCase(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        // Token & User SUDAH DISIMPAN otomatis oleh Repository.
                        // Kita tinggal urus fitur "Last Email" (Remember Me)
                        if (isRemembered) {
                            sessionManager.saveLastEmail(email)
                        } else {
                            // Opsional: Hapus email terakhir jika user tidak mau diingat
                            // sessionManager.saveLastEmail("")
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                userName = result.data?.name ?: "User"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { SignInUiState() }
    }
}