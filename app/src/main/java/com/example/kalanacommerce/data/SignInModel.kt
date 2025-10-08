package com.example.kalanacommerce.data

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

// Payload yang dikirim ke API
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SignInRequest(
    val email: String,
    val password: String
)

// Bagian 'user' dari respons
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class User(
    val id: Int,
    val full_name: String,
    val email: String,
    val phone_number: String,
    val role: String,
    // created_at dan updated_at bisa diabaikan atau diserialisasi
)

// Respons lengkap dari API
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SignInResponse(
    val status: String,
    val message: String,
    val user: User,
    val token: String
)

// Status UI untuk View
data class SignInUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val token: String? = null,
    val user: User? = null
)