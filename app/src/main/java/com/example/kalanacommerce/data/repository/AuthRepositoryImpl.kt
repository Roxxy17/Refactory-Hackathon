// In D:/My-Project/KalanaCommerce/app/src/main/java/com/example/kalanacommerce/back/data/repository/AuthRepositoryImpl.kt

package com.example.kalanacommerce.data.repository

import android.util.Log
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.remote.dto.auth.UserDto
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordResponse
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ResetPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInResponse // <-- Tambahkan import ini
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterResponse
import com.example.kalanacommerce.data.remote.service.AuthService
import com.example.kalanacommerce.domain.repository.AuthRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionManager: SessionManager
) : AuthRepository {

    // --- REGISTER (Tidak ada perubahan) ---
    override suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String
    ): Result<Unit> {
        return try {
            val response = authService.register(
                RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    phoneNumber = phone
                )
            )

            if (response.status) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }

        } catch (e: ClientRequestException) {
            val errorBody = try {
                e.response.body<RegisterResponse>().message
            } catch (_: Exception) {
                "Data yang dikirim tidak valid"
            }
            Result.failure(Exception(errorBody))

        } catch (e: io.ktor.client.plugins.ServerResponseException) {
            Result.failure(Exception("Server sedang dalam perbaikan, silakan coba lagi nanti"))

        } catch (e: Exception) {
            Log.e("AUTH_ERROR", "Exception Detail: ${e.message}")
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi internet Anda."))
        }
    }

    // --- SIGN IN (Perbaikan di sini) ---
    override suspend fun signIn(email: String, password: String): Result<Pair<String, UserDto>> {
        return try {
            val request = SignInRequest(email = email, password = password)
            // 1. Berikan tipe eksplisit pada response
            val response: SignInResponse = authService.signIn(request)

            if (response.status) {
                // 2. Akses token dan user dari dalam properti 'data'
                val token = response.data?.token
                val user = response.data?.user

                // 3. Validasi bahwa keduanya tidak null
                if (token != null && user != null) {
                    // SUKSES: Kembalikan keduanya sebagai Pasangan (Pair)
                    Result.success(Pair(token, user))
                } else {
                    Result.failure(Exception("Login berhasil tapi data user/token kosong dari server"))
                }
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: ClientRequestException) {
            // Tangani error spesifik dari client (misal: email/password salah)
            val errorBody = try {
                e.response.body<SignInResponse>().message
            } catch (_: Exception) {
                "Email atau password salah"
            }
            Result.failure(Exception(errorBody))
        }
        catch (e: Exception) {
            // Tangani error umum lainnya
            Result.failure(Exception("Gagal Login: ${e.message}"))
        }
    }

    // Forgot Password
    override fun forgotPassword(email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.forgotPassword(ForgotPasswordRequest(email))
            emit(Resource.Success(response.message))
        } catch (e: ClientRequestException) {
            val errorMessage = try {
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "Gagal mengirim OTP"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan"))
        }
    }

    // Reset Password
    override fun resetPassword(email: String, otp: String, newPassword: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.resetPassword(ResetPasswordRequest(email, otp, newPassword))
            emit(Resource.Success(response.message))
        } catch (e: ClientRequestException) {
            val errorMessage = try {
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "Gagal mereset password"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan"))
        }
    }

    // --- LOGOUT (Tidak ada perubahan) ---
    override suspend fun logout(): Result<Unit> {
        return try {
            sessionManager.clearAuthData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
