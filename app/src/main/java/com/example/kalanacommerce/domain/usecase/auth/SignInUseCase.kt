package com.example.kalanacommerce.domain.usecase.auth

import com.example.kalanacommerce.data.remote.dto.auth.UserDto
import com.example.kalanacommerce.domain.repository.AuthRepository

class SignInUseCase(private val authRepository: AuthRepository) {
    // Pastikan nilai kembali di sini juga diubah menjadi Result<Pair<String, UserDto>>
    suspend operator fun invoke(email: String, password: String): Result<Pair<String, UserDto>> {
        return authRepository.signIn(email, password)
    }
}

