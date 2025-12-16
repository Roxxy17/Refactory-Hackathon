package com.example.kalanacommerce.back.data.mapper

import com.example.kalanacommerce.back.data.remote.dto.auth.UserDto
import com.example.kalanacommerce.back.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = id ?: throw IllegalStateException("User id kosong"),
        name = name ?: "",
        email = email ?: "",
        phoneNumber = phoneNumber
    )
}

