@file:OptIn(InternalSerializationApi::class) // <--- TAMBAHKAN INI
package com.example.kalanacommerce.data.remote.dto.auth.register

import com.example.kalanacommerce.data.remote.dto.auth.AuthData
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val status: Boolean,
    val statusCode: Int? = null,
    val message: String,
    val data: AuthData? = null
)