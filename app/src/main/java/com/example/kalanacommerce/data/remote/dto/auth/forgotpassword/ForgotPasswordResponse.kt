package com.example.kalanacommerce.data.remote.dto.auth.forgotpassword

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordResponse(
    @SerialName("message")
    val message: String
)