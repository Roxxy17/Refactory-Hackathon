package com.example.kalanacommerce.data.remote.dto.user

import com.example.kalanacommerce.data.remote.dto.adress.AddressDto
import kotlinx.serialization.Serializable

@Serializable
data class ProfileUserDto(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val image: String? = null,
    val balance: String? = null,
    val createdAt: String? = null,
    val addresses: List<AddressDto> = emptyList()
)