package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.User // Pakai Domain
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    // UBAH DARI ProfileUserDto KE User
    fun getProfile(): Flow<Resource<User>>

    fun updateProfile(name: String, email: String, phone: String): Flow<Resource<String>>
    fun updatePhoto(imageBytes: ByteArray): Flow<Resource<String>>
}