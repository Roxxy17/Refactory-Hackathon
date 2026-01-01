package com.example.kalanacommerce.data.repository

import android.util.Log
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.remote.dto.auth.UserDto
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordResponse
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ResetPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInResponse
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterResponse
import com.example.kalanacommerce.data.remote.service.AuthService
import com.example.kalanacommerce.domain.repository.AuthRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException // WAJIB: Import ini untuk handle error 500
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionManager: SessionManager
) : AuthRepository {

    // --- REGISTER ---
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
            // Error 4xx
            val errorBody = try {
                e.response.body<RegisterResponse>().message
            } catch (_: Exception) {
                "Data yang dikirim tidak valid"
            }
            Result.failure(Exception(errorBody))

        } catch (e: ServerResponseException) {
            // Error 5xx
            val errorBody = try {
                e.response.body<RegisterResponse>().message
            } catch (_: Exception) {
                "Server sedang dalam perbaikan"
            }
            Result.failure(Exception(errorBody))

        } catch (e: Exception) {
            Log.e("AUTH_ERROR", "Exception Detail: ${e.message}")
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi internet Anda."))
        }
    }

    // --- SIGN IN ---
    override suspend fun signIn(email: String, password: String): Result<Pair<String, UserDto>> {
        return try {
            val request = SignInRequest(email = email, password = password)
            val response: SignInResponse = authService.signIn(request)

            if (response.status) {
                val token = response.data?.token
                val user = response.data?.user

                if (token != null && user != null) {
                    Result.success(Pair(token, user))
                } else {
                    Result.failure(Exception("Login berhasil tapi data user/token kosong"))
                }
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: ClientRequestException) {
            val errorBody = try {
                e.response.body<SignInResponse>().message
            } catch (_: Exception) {
                "Email atau password salah"
            }
            Result.failure(Exception(errorBody))
        } catch (e: ServerResponseException) {
            val errorBody = try {
                e.response.body<SignInResponse>().message
            } catch (_: Exception) {
                "Terjadi kesalahan di server"
            }
            Result.failure(Exception(errorBody))
        } catch (e: Exception) {
            Result.failure(Exception("Gagal Login: ${e.message}"))
        }
    }

    // --- FORGOT PASSWORD (PERBAIKAN UTAMA DISINI) ---
    override fun forgotPassword(email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.forgotPassword(ForgotPasswordRequest(email))
            // Jika sukses (200 OK)
            emit(Resource.Success(response.message))
        } catch (e: ClientRequestException) {
            // Handle Error 4xx (Misal 404 Not Found, 400 Bad Request)
            val errorMessage = try {
                // Mencoba membaca JSON response body untuk mengambil pesan error dari backend
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "Permintaan tidak valid"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: ServerResponseException) {
            // Handle Error 5xx (Misal 500 Internal Server Error yang berisi "Akun Tidak Ditemukan")
            val errorMessage = try {
                // Mencoba membaca JSON dari error 500
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "Terjadi kesalahan pada server (500)"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            // Error Jaringan / Lainnya
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan koneksi"))
        }
    }

    // --- RESET PASSWORD (PERBAIKAN SAMA) ---
    override fun resetPassword(
        email: String,
        otp: String,
        newPassword: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.resetPassword(ResetPasswordRequest(email, otp, newPassword))
            emit(Resource.Success(response.message))
        } catch (e: ClientRequestException) {
            // Handle 4xx
            val errorMessage = try {
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "Gagal mereset password, cek input Anda"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: ServerResponseException) {
            // Handle 5xx
            val errorMessage = try {
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "Terjadi kesalahan pada server saat reset password"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan koneksi"))
        }
    }

    // --- LOGOUT ---
    override suspend fun logout(): Result<Unit> {
        return try {
            sessionManager.clearAuthData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}