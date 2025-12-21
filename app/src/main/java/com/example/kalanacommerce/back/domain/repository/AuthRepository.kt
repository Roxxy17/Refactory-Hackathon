package com.example.kalanacommerce.back.domain.repository

import com.example.kalanacommerce.back.data.remote.dto.auth.UserDto
import com.example.kalanacommerce.back.domain.model.User

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Pair<String, UserDto>>
    suspend fun register(name: String, email: String, password: String, phoneNumber: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}