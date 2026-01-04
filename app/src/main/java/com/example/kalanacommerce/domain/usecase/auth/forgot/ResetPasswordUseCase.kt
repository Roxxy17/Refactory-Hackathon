package com.example.kalanacommerce.domain.usecase.auth.forgot

import com.example.kalanacommerce.domain.repository.AuthRepository
import com.example.kalanacommerce.core.util.Resource
import kotlinx.coroutines.flow.Flow

class ResetPasswordUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, otp: String, newPassword: String): Flow<Resource<String>> {
        return repository.resetPassword(email, otp, newPassword)
    }
}