package com.example.kalanacommerce.back.data.repository

import android.util.Log
import com.example.kalanacommerce.back.data.local.datastore.SessionManager
import com.example.kalanacommerce.back.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.back.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.back.data.remote.service.AuthService
import com.example.kalanacommerce.back.domain.repository.AuthRepository
import io.ktor.client.plugins.ClientRequestException

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
                Result.failure(IllegalStateException(response.message))
            }

        } catch (e: ClientRequestException) {
            Result.failure(Exception("Request tidak valid"))
        } catch (e: Exception) {
            Log.e("AUTH_ERROR", "Exception Detail: ${e.message}") // Jika Anda punya Logcat
            Result.failure(Exception("Kesalahan Jaringan (Detail: ${e.localizedMessage})"))
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val response = authService.signIn(
                SignInRequest(email = email, password = password)
            )

            // 1. CEK STATUS DULU
            if (response.status) {
                // Jika sukses, baru ambil token
                val token = response.data?.token

                if (token != null) {
                    // 2. SIMPAN SESSION
                    sessionManager.saveAuthData(token, true)
                    Result.success(token)
                } else {
                    Result.failure(Exception("Login sukses tapi token kosong"))
                }
            } else {
                // 3. JIKA GAGAL, TAMPILKAN PESAN DARI BACKEND
                // Ini yang akan memunculkan "Email harus diisi" atau "Password salah"
                Result.failure(Exception(response.message))
            }

        } catch (e: ClientRequestException) {
            Result.failure(Exception("Gagal terhubung ke server (4xx)"))
        } catch (e: Exception) {
            Result.failure(Exception("Gagal Login: ${e.localizedMessage}"))
        }
    }

    // --- LOGOUT ---
    override suspend fun logout(): Result<Unit> {
        return try {
            // sessionManager.clearAuthData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
