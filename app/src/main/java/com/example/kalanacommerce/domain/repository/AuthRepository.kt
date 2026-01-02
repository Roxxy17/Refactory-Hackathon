package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Pastikan semua return-nya Flow<Resource<...>>
    fun register(name: String, email: String, password: String, phone: String): Flow<Resource<String>>

    fun signIn(email: String, password: String): Flow<Resource<ProfileUserDto>>

    fun forgotPassword(email: String): Flow<Resource<String>>

    fun resetPassword(email: String, otp: String, newPassword: String): Flow<Resource<String>>

    suspend fun logout(): Resource<Unit>
}