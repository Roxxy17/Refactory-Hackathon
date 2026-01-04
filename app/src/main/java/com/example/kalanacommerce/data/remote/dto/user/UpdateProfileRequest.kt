package com.example.kalanacommerce.data.remote.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phoneNumber: String
)