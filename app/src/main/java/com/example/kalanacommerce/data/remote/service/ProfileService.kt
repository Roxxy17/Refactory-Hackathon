package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.BaseResponse // <--- Import BaseResponse
import com.example.kalanacommerce.data.remote.dto.user.UpdateProfileRequest
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import io.ktor.client.statement.HttpResponse
import io.ktor.http.content.PartData

interface ProfileService {
    // UBAH DARI: suspend fun getProfile(): UserDto
    // MENJADI:
    suspend fun getProfile(): BaseResponse<ProfileUserDto>

    suspend fun updateProfile(request: UpdateProfileRequest): HttpResponse
    suspend fun updatePhoto(formData: List<PartData>): HttpResponse
}