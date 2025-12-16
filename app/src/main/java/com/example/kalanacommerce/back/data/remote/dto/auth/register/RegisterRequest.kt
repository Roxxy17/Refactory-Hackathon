@file:OptIn(InternalSerializationApi::class) // <--- TAMBAHKAN INI
package com.example.kalanacommerce.back.data.remote.dto.auth.register

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String
)