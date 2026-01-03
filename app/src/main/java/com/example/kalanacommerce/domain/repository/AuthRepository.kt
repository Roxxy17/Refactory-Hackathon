package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.User // Pakai Domain
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun register(name: String, email: String, password: String, phone: String): Flow<Resource<String>>

    // UBAH DARI ProfileUserDto KE User
    fun signIn(email: String, password: String): Flow<Resource<User>>

    fun forgotPassword(email: String): Flow<Resource<String>>
    fun resetPassword(email: String, otp: String, newPassword: String): Flow<Resource<String>>
    suspend fun logout(): Resource<Unit>
}