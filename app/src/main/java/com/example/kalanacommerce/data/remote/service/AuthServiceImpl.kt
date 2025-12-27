package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.BuildConfig
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInResponse
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthServiceImpl(
    private val client: HttpClient
) : AuthService {

    private val BASE_URL = BuildConfig.API_BASE_URL

    override suspend fun register(
        request: RegisterRequest
    ): RegisterResponse {
        return client.post("$BASE_URL/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun signIn(
        request: SignInRequest
    ): SignInResponse {
        return client.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}