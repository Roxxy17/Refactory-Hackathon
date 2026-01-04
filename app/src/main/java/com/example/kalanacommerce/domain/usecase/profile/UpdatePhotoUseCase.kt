package com.example.kalanacommerce.domain.usecase.profile

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class UpdatePhotoUseCase(private val repository: ProfileRepository) {
    operator fun invoke(imageBytes: ByteArray): Flow<Resource<String>> {
        return repository.updatePhoto(imageBytes)
    }
}