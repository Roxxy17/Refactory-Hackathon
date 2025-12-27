package com.example.kalanacommerce.domain.usecase.auth

import com.example.kalanacommerce.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
