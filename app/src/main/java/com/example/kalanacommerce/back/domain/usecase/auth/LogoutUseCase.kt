package com.example.kalanacommerce.back.domain.usecase.auth

import com.example.kalanacommerce.back.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
