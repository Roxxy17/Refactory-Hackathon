package com.example.kalanacommerce.data.remote.dto.auth.forgotpassword

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequest(
    @SerialName("email")
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    @SerialName("email")
    val email: String,
    @SerialName("otp")
    val otp: String,
    @SerialName("newPassword")
    val newPassword: String
)