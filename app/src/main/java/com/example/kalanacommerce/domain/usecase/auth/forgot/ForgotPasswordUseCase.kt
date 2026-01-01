package com.example.kalanacommerce.domain.usecase.auth.forgot

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ForgotPasswordUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String): Flow<Resource<String>> {
        return repository.forgotPassword(email)
    }
}