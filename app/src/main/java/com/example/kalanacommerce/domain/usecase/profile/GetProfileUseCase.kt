package com.example.kalanacommerce.domain.usecase.profile

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import com.example.kalanacommerce.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(): Flow<Resource<ProfileUserDto>> {
        return repository.getProfile()
    }
}