package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.auth.AuthUserDto
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto

fun AuthUserDto.toUserDto(): ProfileUserDto {
    return ProfileUserDto(
        id = this.id,
        name = this.name,
        email = this.email,
        phoneNumber = this.phoneNumber,
        createdAt = null,
        balance = "0",
        image = null,
        addresses = emptyList()
    )
}