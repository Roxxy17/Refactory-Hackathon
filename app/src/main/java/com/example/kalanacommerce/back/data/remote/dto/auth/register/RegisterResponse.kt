@file:OptIn(InternalSerializationApi::class) // <--- TAMBAHKAN INI
package com.example.kalanacommerce.back.data.remote.dto.auth.register

import com.example.kalanacommerce.back.data.remote.dto.auth.UserDto
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val status: Boolean,
    val statusCode: Int? = null,
    val message: String,
    val data: UserDto? = null
)