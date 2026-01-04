package com.example.kalanacommerce.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val status: Boolean,
    val statusCode: Int? = null,
    val message: String? = null,
    val data: T? = null
)