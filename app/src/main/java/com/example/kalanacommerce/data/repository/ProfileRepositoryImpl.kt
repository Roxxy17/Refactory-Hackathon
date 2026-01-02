package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.user.UpdateProfileRequest
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import com.example.kalanacommerce.data.remote.service.ProfileService
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

    override fun getProfile(): Flow<Resource<ProfileUserDto>> = flow {
        emit(Resource.Loading())
        try {
            val response = profileService.getProfile()
            if (response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error("Data kosong: ${response.message}"))
            }
        } catch (e: Exception) {
            // Gunakan helper error detail
            emit(Resource.Error(getDetailedErrorMessage(e)))
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

    // --- HELPER: LOGGING LENGKAP UNTUK USER/TOAST ---
    private fun getDetailedErrorMessage(e: Exception): String {
        return when (e) {
            // Error 4xx (Salah Request, Data Kegedean, dll)
            is ClientRequestException -> {
                val status = e.response.status.value
                val desc = e.response.status.description
                "Gagal (Client $status): $desc. \nDetail: ${e.message}"
            }
            // Error 5xx (Server Meledak/Down)
            is ServerResponseException -> {
                val status = e.response.status.value
                "Gagal (Server $status): Maaf, server sedang bermasalah. \nDetail: ${e.message}"
            }
            // Error 3xx (Redirect aneh)
            is RedirectResponseException -> {
                "Gagal (Redirect): Terjadi pengalihan tidak terduga. \nDetail: ${e.message}"
            }
            // Error Koneksi (Timeout)
            is TimeoutException, is SocketTimeoutException -> {
                "Waktu Habis (Timeout): Koneksi lambat atau server tidak merespon. Cek internetmu."
            }
            // Error Lainnya
            else -> {
                "Error Tidak Dikenal: ${e.localizedMessage ?: e.toString()}"
            }
        }
    }
}