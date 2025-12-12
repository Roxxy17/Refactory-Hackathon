package com.example.kalanacommerce.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- REQUEST ---
@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    // Tetap String agar nol di depan (08xx) tidak hilang.
    // Pastikan validasi di UI hanya menerima angka.
    val phoneNumber: String
)

// --- RESPONSE (Unified) ---
// Gunakan satu model ini untuk Sukses maupun Error (422/400/500)
@Serializable
data class RegisterResponse(
    val status: Boolean,      // PENTING: Ubah String jadi Boolean
    val statusCode: Int,      // Tambahan dari backend
    val message: String,      // Penjelasan error/sukses
    val data: User? = null    // Gunakan 'User' jika 'data' langsung berisi profil user
)

// --- UI STATE ---
data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)