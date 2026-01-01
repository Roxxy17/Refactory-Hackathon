package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.auth.UserDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun forgotPassword(email: String): Flow<Resource<String>>
    fun resetPassword(email: String, otp: String, newPassword: String): Flow<Resource<String>>
    suspend fun signIn(email: String, password: String): Result<Pair<String, UserDto>>
    suspend fun register(name: String, email: String, password: String, phoneNumber: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}