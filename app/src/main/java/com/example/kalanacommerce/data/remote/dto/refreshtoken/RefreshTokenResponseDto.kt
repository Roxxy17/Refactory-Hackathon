package com.example.kalanacommerce.data.remote.dto.refreshtoken

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponseDto(
    val status: Boolean,
    val message: String,
    val data: TokenDataDto
)