package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.BaseResponse // <--- Pastikan ada import ini
import com.example.kalanacommerce.data.remote.dto.user.UpdateProfileRequest
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.HttpResponse
import io.ktor.http.content.PartData

class ProfileServiceImpl(
    private val client: HttpClient
) : ProfileService {

    // Return type harus sama dengan Interface: BaseResponse<UserDto>
    override suspend fun getProfile(): BaseResponse<ProfileUserDto> {
        return client.get("me").body()
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): HttpResponse {
        return client.put("me/update") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun updatePhoto(formData: List<PartData>): HttpResponse {
        return client.put("me/update-photo") {
            setBody(MultiPartFormDataContent(formData))
        }
    }
}