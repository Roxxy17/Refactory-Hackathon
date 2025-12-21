// In D:/My-Project/KalanaCommerce/app/src/main/java/com/example/kalanacommerce/front/screen/auth/login/SignInViewModel.kt

package com.example.kalanacommerce.front.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // <-- Pastikan hanya ada satu baris ini
import com.example.kalanacommerce.back.data.local.datastore.SessionManager
// Hapus import UserDto jika tidak digunakan secara langsung di sini, atau pastikan ada jika 'user' diketik secara eksplisit
import com.example.kalanacommerce.back.domain.usecase.auth.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUseCase: SignInUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // NOTE: Anda HARUS mengubah AuthRepository dan SignInUseCase
            // untuk mengembalikan Result<Pair<String, UserDto>> agar kode ini berfungsi.
            signInUseCase(email, password)
                .onSuccess { (token, user) -> // Sekarang ini akan berfungsi jika UseCase mengembalikan Pair
                    // Simpan sesi dengan menyertakan data pengguna
                    sessionManager.saveAuthData(token, true, user) // <-- Teruskan 'user'

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

    fun resetState() {
        _uiState.update { SignInUiState() }
    }
}
