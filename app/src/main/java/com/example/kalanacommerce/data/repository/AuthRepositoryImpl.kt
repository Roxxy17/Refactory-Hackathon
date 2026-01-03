package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.mapper.toDomain
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordResponse
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ResetPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterResponse
import com.example.kalanacommerce.data.remote.service.AuthService
import com.example.kalanacommerce.domain.model.User
import com.example.kalanacommerce.domain.repository.AuthRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override fun register(
        name: String, email: String, password: String, phone: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.register(
                RegisterRequest(name, email, password, phone)
            )
            if (response.status) {
                emit(Resource.Success(response.message))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            // Kita coba parse body error spesifik untuk RegisterResponse
            emit(Resource.Error(handleAuthError<RegisterResponse>(e)))
        }
    }

    override fun signIn(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val request = SignInRequest(email, password)
            val response = authService.signIn(request)

            if (response.status) {
                val token = response.data?.token
                val authUser = response.data?.user

                if (token != null && authUser != null) {
                    val userDomain = authUser.toDomain(token)
                    emit(Resource.Success(userDomain))
                } else {
                    emit(Resource.Error("Data akun tidak lengkap dari server."))
                }
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            // SignIn mungkin tidak punya response body error yang standar, jadi kita pakai generic String
            // Atau kalau backendmu return JSON error format standar, bisa pakai handleAuthError<Any>
            emit(Resource.Error(handleAuthError<Any>(e)))
        }
    }

    override fun forgotPassword(email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.forgotPassword(ForgotPasswordRequest(email))
            emit(Resource.Success(response.message))
        } catch (e: Exception) {
            emit(Resource.Error(handleAuthError<ForgotPasswordResponse>(e)))
        }
    }

    override fun resetPassword(
        email: String, otp: String, newPassword: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.resetPassword(ResetPasswordRequest(email, otp, newPassword))
            emit(Resource.Success(response.message))
        } catch (e: Exception) {
            emit(Resource.Error(handleAuthError<ForgotPasswordResponse>(e)))
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            sessionManager.clearAuthData()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal Logout: ${e.localizedMessage}")
        }
    }

    // --- HELPER KHUSUS AUTH (Smart Error Handling) ---
    // Menggunakan reified T agar bisa mencoba mengambil message dari JSON Error spesifik
    private suspend inline fun <reified T> handleAuthError(e: Exception): String {
        return when (e) {
            is ClientRequestException -> {
                // Error 4xx: Coba baca body response server (misal: "Email sudah dipakai")
                try {
                    // Asumsi T punya field 'message'. Jika struktur error beda, sesuaikan disini.
                    // Cara paling aman jika T tidak pasti: Ambil sbg String map
                    val errorBody = e.response.body<T>()
                    // Reflection simple atau casting manual tergantung struktur DTO error kamu
                    // Disini saya return pesan generic "Data tidak valid" kecuali kita tahu pasti T punya .message
                    // Tapi untuk amannya, kita return pesan default yang rapi berdasarkan status code:
                    when (e.response.status.value) {
                        400 -> "Input tidak valid. Cek kembali data Anda."
                        401 -> "Email atau password salah."
                        403 -> "Akses ditolak."
                        404 -> "Akun tidak ditemukan."
                        409 -> "Data sudah terdaftar (Email/No HP)." // Conflict
                        422 -> "Format data salah."
                        else -> "Gagal memproses permintaan (${e.response.status.value})."
                    }
                } catch (parseEx: Exception) {
                    "Permintaan gagal diproses."
                }
            }
            is ServerResponseException -> "Server sedang bermasalah (500). Coba lagi nanti."
            is RedirectResponseException -> "Terjadi kesalahan pengalihan sistem."
            is TimeoutException, is SocketTimeoutException -> "Koneksi time out. Cek internet Anda."
            is IOException -> "Tidak ada koneksi internet."
            else -> "Terjadi kesalahan tidak dikenal."
        }
    }
}