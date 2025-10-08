package com.example.kalanacommerce.data

import com.example.kalanacommerce.data.RegisterRequest
import com.example.kalanacommerce.data.RegisterResponse
import com.example.kalanacommerce.data.SignInRequest
import com.example.kalanacommerce.data.SignInResponse

/**
 * Interface untuk repository otentikasi.
 * Ini menjadi perantara antara ViewModel dan sumber data (AuthService).
 */
interface AuthRepository {

    /**
     * Mengirimkan permintaan login ke sumber data.
     * @param signInRequest Objek yang berisi email dan password.
     * @return SignInResponse jika berhasil.
     * @throws Exception jika permintaan gagal (misalnya, error jaringan atau kredensial salah).
     */
    suspend fun signIn(signInRequest: SignInRequest): SignInResponse

    /**
     * Mengirimkan permintaan registrasi ke sumber data.
     * @param registerRequest Objek yang berisi data pendaftaran.
     * @return RegisterResponse jika berhasil.
     * @throws Exception jika permintaan gagal.
     */
    suspend fun register(registerRequest: RegisterRequest): RegisterResponse
}
