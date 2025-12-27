package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterResponse
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInResponse


interface AuthService {

    suspend fun register(
        request: RegisterRequest
    ): RegisterResponse

    suspend fun signIn(
        request: SignInRequest
    ): SignInResponse
}
