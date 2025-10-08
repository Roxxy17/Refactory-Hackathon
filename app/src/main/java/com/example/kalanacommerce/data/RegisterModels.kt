package com.example.kalanacommerce.data

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

// Payload yang dikirim ke /auth/register
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class RegisterRequest(
    val full_name: String,
    val email: String,
    val password: String,
    val phone_number: String,
    val role: String
)

// Respons pendaftaran (kita bisa menggunakan User dari model SignIn, tapi dibuat baru untuk kejelasan)
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class RegisterResponse(
    val status: String,
    val message: String,
    val user: User
)

// Model untuk Error (sesuai format server: {"status":"Gagal", "error":"..."})
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ServerErrorResponse(
    val status: String,
    val error: String?
)

// Status UI untuk View Pendaftaran
data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)