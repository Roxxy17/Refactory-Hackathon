package com.example.kalanacommerce.domain.usecase.profile

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.User
import com.example.kalanacommerce.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(): Flow<Resource<User>> {
        return repository.getProfile()
    }
}