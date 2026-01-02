package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    // Mengambil data user
    fun getProfile(): Flow<Resource<ProfileUserDto>>

    // Update data teks (Nama, Email, HP)
    fun updateProfile(name: String, email: String, phone: String): Flow<Resource<String>>

    // Update foto (Upload ByteArray)
    fun updatePhoto(imageBytes: ByteArray): Flow<Resource<String>>
}