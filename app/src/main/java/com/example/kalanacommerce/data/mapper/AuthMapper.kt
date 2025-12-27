package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.auth.UserDto
import com.example.kalanacommerce.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = id ?: throw IllegalStateException("User id kosong"),
        name = name ?: "",
        email = email ?: "",
        phoneNumber = phoneNumber
    )
}

