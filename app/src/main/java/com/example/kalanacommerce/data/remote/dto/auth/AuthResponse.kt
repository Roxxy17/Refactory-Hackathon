package com.example.kalanacommerce.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthData( // Anda bisa menamakan ini AuthData atau LoginData
    val token: String? = null,
    val user: UserDto? = null
)