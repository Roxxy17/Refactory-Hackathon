@file:OptIn(InternalSerializationApi::class) // <--- TAMBAHKAN INI
package com.example.kalanacommerce.back.data.remote.dto.auth.login

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email: String,
    val password: String
)