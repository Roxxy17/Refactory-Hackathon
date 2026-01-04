package com.example.kalanacommerce.presentation.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.mapper.toDto // <--- [PENTING] Import Mapper Balik ini!
import com.example.kalanacommerce.domain.model.User // <--- Pastikan pakai User (Domain), bukan DTO
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
            // Panggil UseCase (Sekarang mengembalikan Flow<Resource<User>>)
            signInUseCase(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        val userDomain: User? = result.data

                        if (userDomain != null) {
                            // --- BAGIAN INI YANG BERUBAH ---

                            // 1. Ambil Token dari Domain Model
                            val token = userDomain.token ?: ""

                            // 2. Konversi 'User' (Domain) kembali ke 'ProfileUserDto'
                            // agar bisa disimpan oleh SessionManager
                            val userDto = userDomain.toDto()

                            // 3. Simpan ke Session
                            sessionManager.saveSession(token, userDto)

                            // 4. Update Remember Me
                            if (isRemembered) {
                                sessionManager.saveLastEmail(email)
                            }
                            // -------------------------------

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    userName = userDomain.name // Pakai data domain langsung buat UI
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(isLoading = false, error = "Data user kosong")
                            }
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