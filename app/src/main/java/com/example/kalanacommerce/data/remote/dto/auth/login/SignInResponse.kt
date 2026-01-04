@file:OptIn(InternalSerializationApi::class) // <--- TAMBAHKAN INI
package com.example.kalanacommerce.data.remote.dto.auth.login

import com.example.kalanacommerce.data.remote.dto.auth.AuthData
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse( // Bungkus Luar (Envelope)
    val status: Boolean,
    val statusCode: Int,
    val message: String,
    val data: AuthData? = null, // Isi Paket (Data Utama)
    val token: String? = null
)