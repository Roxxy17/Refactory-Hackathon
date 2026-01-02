package com.example.kalanacommerce.domain.usecase.auth

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    // Tetap 'suspend', tapi return jadi Resource<Unit>
    suspend operator fun invoke(): Resource<Unit> {
        return authRepository.logout()
    }
}