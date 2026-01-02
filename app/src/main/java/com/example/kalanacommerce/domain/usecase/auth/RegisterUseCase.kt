package com.example.kalanacommerce.domain.usecase.auth

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    // Hapus 'suspend', ubah return jadi Flow<Resource<String>>
    operator fun invoke(
        name: String,
        email: String,
        password: String,
        phone: String
    ): Flow<Resource<String>> {
        return authRepository.register(name, email, password, phone)
    }
}