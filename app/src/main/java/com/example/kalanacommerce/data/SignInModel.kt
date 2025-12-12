package com.example.kalanacommerce.data

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
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
    // Asumsi properti ini wajib, tapi mungkin TIDAK dikirim saat sukses atau di respons lain.
    // Jadikan semua properti di sini nullable dan berikan default value.
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    // Field 'message' yang juga disebut di error log mungkin seharusnya tidak ada di sini,
    // jika 'message' adalah properti tingkat atas. Hapus 'message' dari User jika memang begitu.
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
    val token: String? = null
)