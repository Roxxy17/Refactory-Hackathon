package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.auth.AuthUserDto
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import com.example.kalanacommerce.domain.model.User

// --- 1. DARI API LOGIN (AuthUserDto) -> DOMAIN ---
// (Menggantikan fungsi toUserDto() yang lama)
fun AuthUserDto.toDomain(token: String?): User {
    return User(
        id = this.id.orEmpty(),
        name = this.name ?: "No Name",
        email = this.email.orEmpty(),
        phoneNumber = this.phoneNumber.orEmpty(),
        image = null, // Login biasanya belum return image
        balance = "0",
        token = token // Token dimasukkan ke Domain Model agar mudah dibawa
    )
}

// --- 2. DARI API PROFILE (ProfileUserDto) -> DOMAIN ---
fun ProfileUserDto.toDomain(): User {
    return User(
        id = this.id.orEmpty(),
        name = this.name ?: "No Name",
        email = this.email.orEmpty(),
        phoneNumber = this.phoneNumber.orEmpty(),
        image = this.image,
        balance = this.balance ?: "0",
        token = null
    )
}

// --- 3. DARI DOMAIN -> KEMBALI KE DTO ---
// (Penting: Karena SessionManager kamu menyimpan JSON ProfileUserDto)
fun User.toDto(): ProfileUserDto {
    return ProfileUserDto(
        id = this.id,
        name = this.name,
        email = this.email,
        phoneNumber = this.phoneNumber,
        image = this.image,
        balance = this.balance,
        // Field lain bisa diisi default/null karena tidak dipakai UI login
        createdAt = null,
        addresses = emptyList()
    )
}