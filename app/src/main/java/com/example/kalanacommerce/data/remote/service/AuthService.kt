package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordResponse
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ResetPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterResponse
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInResponse
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthService {

    suspend fun forgotPassword(
        request: ForgotPasswordRequest)
    : ForgotPasswordResponse

    suspend fun resetPassword(
        request: ResetPasswordRequest
    ): ForgotPasswordResponse

    suspend fun register(
        request: RegisterRequest
    ): RegisterResponse

    suspend fun signIn(
        request: SignInRequest
    ): SignInResponse
}
