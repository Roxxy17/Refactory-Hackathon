package com.example.kalanacommerce.domain.usecase.profile

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class UpdateProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(name: String, email: String, phone: String): Flow<Resource<String>> {
        return repository.updateProfile(name, email, phone)
    }
}