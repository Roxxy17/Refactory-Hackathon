package com.example.kalanacommerce.data.remote.dto.refreshtoken

import kotlinx.serialization.Serializable

@Serializable
data class TokenDataDto(
    val accessToken: String,
    val refreshToken: String
)