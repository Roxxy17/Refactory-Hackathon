package com.example.kalanacommerce.domain.usecase.auth

import com.example.kalanacommerce.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        phone: String
    ): Result<Unit> {
        return authRepository.register(
            name = name,
            email = email,
            password = password,
            phoneNumber = phone
        )
    }
}
