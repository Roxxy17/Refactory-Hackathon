package com.example.kalanacommerce.data



import android.annotation.SuppressLint
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LoginRequest(val email: String, val password: String)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class UserData(
    val id: Int,
    val full_name: String,
    val email: String,
    val phone_number: String?,
    val role: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LoginResponse(
    val status: String,
    val message: String,
    val user: UserData,
    val token: String
)



interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}