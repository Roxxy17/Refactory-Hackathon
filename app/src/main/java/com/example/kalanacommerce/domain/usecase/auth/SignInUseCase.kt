package com.example.kalanacommerce.domain.usecase.auth

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.User
import com.example.kalanacommerce.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SignInUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<Resource<User>> {
        return authRepository.signIn(email, password)
    }
}