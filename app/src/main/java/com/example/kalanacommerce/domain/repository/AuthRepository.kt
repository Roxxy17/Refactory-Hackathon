package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.data.remote.dto.auth.UserDto

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Pair<String, UserDto>>
    suspend fun register(name: String, email: String, password: String, phoneNumber: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}