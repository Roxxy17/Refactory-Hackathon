package com.example.kalanacommerce.domain.usecase.auth

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import com.example.kalanacommerce.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SignInUseCase(private val authRepository: AuthRepository) {

    // Hapus 'suspend', ubah return jadi Flow<Resource<UserDto>>
    operator fun invoke(email: String, password: String): Flow<Resource<ProfileUserDto>> {
        return authRepository.signIn(email, password)
    }
}