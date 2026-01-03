package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.mapper.toDomain
import com.example.kalanacommerce.data.remote.dto.user.UpdateProfileRequest
import com.example.kalanacommerce.data.remote.service.ProfileService
import com.example.kalanacommerce.domain.model.User
import com.example.kalanacommerce.domain.repository.ProfileRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ProfileRepositoryImpl(
    private val profileService: ProfileService
) : ProfileRepository {

    override fun getProfile(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = profileService.getProfile() // Return BaseResponse<ProfileUserDto>
            if (response.data != null) {
                // MAPPING DTO -> DOMAIN
                val userDomain = response.data.toDomain()
                emit(Resource.Success(userDomain))
            } else {
                emit(Resource.Error("Data kosong"))
            }
        } catch (e: Exception) {
            // ... error handling ...
            emit(Resource.Error("Gagal load profil"))
        }
    }

    override fun updateProfile(name: String, email: String, phone: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val request = UpdateProfileRequest(name, email, phone)
            profileService.updateProfile(request)
            emit(Resource.Success("Profil berhasil diperbarui"))
        } catch (e: Exception) {
            emit(Resource.Error(getDetailedErrorMessage(e)))
        }
    }

    override fun updatePhoto(imageBytes: ByteArray): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val multipartData = formData {
                append("image", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                })
            }
            profileService.updatePhoto(multipartData)
            emit(Resource.Success("Foto berhasil diperbarui"))
        } catch (e: Exception) {
            emit(Resource.Error(getDetailedErrorMessage(e)))
        }
    }

    // --- HELPER: LOGGING LENGKAP & BERSIH UNTUK USER ---
    private fun getDetailedErrorMessage(e: Exception): String {
        return when (e) {
            // Error 4xx (Salah Request, Data Kegedean, Auth)
            is ClientRequestException -> {
                val status = e.response.status.value
                when (status) {
                    413 -> "Gagal: Ukuran foto terlalu besar. Harap kompres foto atau pilih yang lebih kecil."
                    401 -> "Gagal: Sesi telah berakhir. Silakan login ulang."
                    400, 422 -> "Gagal: Data tidak valid. Cek kembali inputan Anda."
                    else -> "Gagal ($status): Permintaan tidak dapat diproses."
                }
            }

            // Error 5xx (Server Meledak/Down)
            is ServerResponseException -> {
                val status = e.response.status.value
                "Maaf, server sedang bermasalah ($status). Silakan coba lagi nanti."
            }

            // Error 3xx (Redirect aneh)
            is RedirectResponseException -> {
                "Terjadi kesalahan pengalihan jaringan."
            }

            // Error Koneksi (Timeout / No Internet)
            is TimeoutException, is SocketTimeoutException -> {
                "Waktu habis. Koneksi internet Anda lambat atau tidak stabil."
            }

            // Error Java/IO (Biasanya tidak ada internet)
            is java.io.IOException -> {
                "Gagal terhubung. Periksa koneksi internet Anda."
            }

            // Error Lainnya
            else -> {
                // Hapus detail teknis panjang, ambil localizedMessage singkat saja
                "Terjadi kesalahan: ${e.message?.take(50) ?: "Tidak diketahui"}"
            }
        }
    }
}