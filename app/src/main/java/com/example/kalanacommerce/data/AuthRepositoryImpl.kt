package com.example.kalanacommerce.data

import com.example.kalanacommerce.data.RegisterRequest
import com.example.kalanacommerce.data.RegisterResponse
import com.example.kalanacommerce.data.SignInRequest
import com.example.kalanacommerce.data.SignInResponse

/**
 * Implementasi dari AuthRepository yang menggunakan AuthService (Ktor)
 * sebagai sumber data.
 * @param authService Instance dari AuthService yang bertanggung jawab atas panggilan jaringan.
 */
class AuthRepositoryImpl(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun signIn(signInRequest: SignInRequest): SignInResponse {
        // Panggil fungsi signIn dari authService
        val result = authService.signIn(signInRequest)

        // Cek hasilnya. Jika berhasil, kembalikan datanya.
        // Jika gagal, lemparkan (throw) exception yang ada di dalam Result.
        // ViewModel kemudian akan menangkap (catch) exception ini.
        return result.getOrThrow()
    }

    override suspend fun register(registerRequest: RegisterRequest): RegisterResponse {
        // Panggil fungsi register dari authService
        val result = authService.register(registerRequest)

        // Lakukan hal yang sama seperti pada signIn
        return result.getOrThrow()
    }
}
