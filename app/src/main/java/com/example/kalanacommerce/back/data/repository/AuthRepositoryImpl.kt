package com.example.kalanacommerce.back.data.repository

import android.util.Log
import com.example.kalanacommerce.back.data.local.datastore.SessionManager
import com.example.kalanacommerce.back.data.remote.dto.auth.login.SignInRequest
import com.example.kalanacommerce.back.data.remote.dto.auth.register.RegisterRequest
import com.example.kalanacommerce.back.data.remote.dto.auth.register.RegisterResponse
import com.example.kalanacommerce.back.data.remote.service.AuthService
import com.example.kalanacommerce.back.domain.repository.AuthRepository
import io.ktor.client.call.body
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

            // 1. Cek status sukses dari backend
            if (response.status) {
                Result.success(Unit)
            } else {
                // 2. Jika status false, gunakan pesan dari backend (misal: "Nomor telepon wajib diisi")
                Result.failure(Exception(response.message))
            }

        } catch (e: ClientRequestException) {
            // 3. Menangani error 4xx (400, 401, 409, 422)
            // Coba ambil body error jika ada, jika tidak gunakan pesan default
            val errorBody = try {
                e.response.body<RegisterResponse>().message
            } catch (_: Exception) {
                "Data yang dikirim tidak valid"
            }
            Result.failure(Exception(errorBody))

        } catch (e: io.ktor.client.plugins.ServerResponseException) {
            // 4. Menangani error 5xx (Server Crash)
            Result.failure(Exception("Server sedang dalam perbaikan, silakan coba lagi nanti"))

        } catch (e: Exception) {
            // 5. Menangani kesalahan koneksi (Timeout, No Internet)
            Log.e("AUTH_ERROR", "Exception Detail: ${e.message}")
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi internet Anda."))
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
            Result.failure(Exception("Gagal Login: ${e.message}"))
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
