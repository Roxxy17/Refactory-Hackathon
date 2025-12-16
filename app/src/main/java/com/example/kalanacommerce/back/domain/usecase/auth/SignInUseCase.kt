package com.example.kalanacommerce.back.domain.usecase.auth

import com.example.kalanacommerce.back.domain.repository.AuthRepository

class SignInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<String> {
        return repository.signIn(email, password)
    }
}

