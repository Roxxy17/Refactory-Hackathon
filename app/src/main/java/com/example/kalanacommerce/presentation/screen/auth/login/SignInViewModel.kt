package com.example.kalanacommerce.presentation.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // --- TAMBAHAN 1: Expose email terakhir yang tersimpan ---
    // UI akan mengamati ini untuk mengisi kolom email secara otomatis
    val lastEmail: StateFlow<String?> = sessionManager.lastEmail
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun signIn(email: String, password: String, isRemembered: Boolean) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            signInUseCase(email, password)
                .onSuccess { (token, user) ->
                    // Simpan data sesi utama (Token, Login State, User Object)
                    sessionManager.saveAuthData(token, isRemembered, user)

                    // --- TAMBAHAN 2: Logika "Remember Me" untuk Email ---
                    // Jika user mencentang Remember Me, kita simpan emailnya secara khusus
                    // agar nanti bisa ditampilkan lagi (Pre-fill) saat logout/buka aplikasi lagi.
                    if (isRemembered) {
                        sessionManager.saveLastEmail(email)
                    } else {
                        // Opsional: Jika tidak dicentang, apakah ingin menghapus history email?
                        // sessionManager.saveLastEmail("") 
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            userName = user.name
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

    fun resetState() {
        _uiState.update { SignInUiState() }
    }
}