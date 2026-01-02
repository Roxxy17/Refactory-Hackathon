package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.mapper.toUserDto // <--- PENTING: Import Mapper
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ForgotPasswordResponse
import com.example.kalanacommerce.data.remote.dto.auth.forgotpassword.ResetPasswordRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.data.remote.dto.auth.login.SignInResponse
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterResponse
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import com.example.kalanacommerce.data.remote.service.AuthService
import com.example.kalanacommerce.domain.repository.AuthRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionManager: SessionManager
) : AuthRepository {

    // --- REGISTER (Ubah ke Flow agar konsisten) ---
    override fun register(
        name: String, email: String, password: String, phone: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.register(
                RegisterRequest(name, email, password, phone)
            )

            if (response.status) {
                // Register sukses
                emit(Resource.Success(response.message))
            } else {
                emit(Resource.Error(response.message))
            }

        } catch (e: ClientRequestException) {
            val errorMessage = try {
                e.response.body<RegisterResponse>().message
            } catch (ex: Exception) {
                "Data tidak valid"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: ServerResponseException) {
            val errorMessage = try {
                e.response.body<RegisterResponse>().message
            } catch (ex: Exception) {
                "Server sedang dalam perbaikan (500)"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Terjadi kesalahan"))
        }
    }

    // --- SIGN IN (Perbaikan Utama: Mapper + Session) ---
    override fun signIn(email: String, password: String): Flow<Resource<ProfileUserDto>> = flow {
        emit(Resource.Loading())
        try {
            val request = SignInRequest(email, password)
            val response = authService.signIn(request)

            if (response.status) {
                val token = response.data?.token
                val authUser = response.data?.user // Ini AuthUserDto (Snake Case)

                if (token != null && authUser != null) {
                    // 1. Mapping AuthUserDto -> UserDto (Camel Case + Balance)
                    val userDto = authUser.toUserDto()

                    // 2. Simpan Sesi (INI YANG SEBELUMNYA HILANG)
                    sessionManager.saveSession(token, userDto)

                    // 3. Emit Data User Lengkap ke UI
                    emit(Resource.Success(userDto))
                } else {
                    emit(Resource.Error("Login berhasil tapi data tidak lengkap"))
                }
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: ClientRequestException) {
            val errorMessage = try {
                e.response.body<SignInResponse>().message
            } catch (ex: Exception) {
                "Email atau password salah"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: ServerResponseException) {
            val errorMessage = try {
                e.response.body<SignInResponse>().message
            } catch (ex: Exception) {
                "Terjadi kesalahan di server"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Login gagal"))
        }
    }

    // --- FORGOT PASSWORD ---
    override fun forgotPassword(email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = authService.forgotPassword(ForgotPasswordRequest(email))
            emit(Resource.Success(response.message))
        } catch (e: ClientRequestException) {
            val errorMessage = try {
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "Email tidak ditemukan"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: ServerResponseException) {
            val errorMessage = try {
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "Server error"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Gagal terhubung"))
        }
    }

    // --- RESET PASSWORD ---
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
            val errorMessage = try {
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "OTP Salah atau Kadaluarsa"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: ServerResponseException) {
            val errorMessage = try {
                e.response.body<ForgotPasswordResponse>().message
            } catch (ex: Exception) {
                "Server error"
            }
            emit(Resource.Error(errorMessage))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Gagal terhubung"))
        }
    }

    // --- LOGOUT ---
    override suspend fun logout(): Resource<Unit> { // Ubah Result ke Resource biar konsisten, atau biarkan suspend
        return try {
            sessionManager.clearAuthData()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Logout gagal")
        }
    }
}