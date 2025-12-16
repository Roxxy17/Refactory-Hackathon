package com.example.kalanacommerce.back.data.remote.service

import com.example.kalanacommerce.back.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.back.data.remote.dto.auth.register.RegisterResponse
import com.example.kalanacommerce.back.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.back.data.remote.dto.auth.login.SignInResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface AuthService {

    suspend fun register(
        request: RegisterRequest
    ): RegisterResponse

    suspend fun signIn(
        request: SignInRequest
    ): SignInResponse
}
