@file:OptIn(InternalSerializationApi::class) // <--- TAMBAHKAN INI
package com.example.kalanacommerce.data.remote.dto.auth

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthUserDto(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null
)