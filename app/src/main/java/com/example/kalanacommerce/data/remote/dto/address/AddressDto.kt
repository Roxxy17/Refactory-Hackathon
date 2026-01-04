package com.example.kalanacommerce.data.remote.dto.address

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val id: String,
    val userId: String,
    val label: String,
    val recipientName: String,
    val phoneNumber: String,

    // Field Detail
    val street: String,
    val postalCode: String,
    val provincesId: String?,
    val citiesId: String?,
    val districtsId: String?, // Tambahkan ini agar tidak error 'Unresolved reference districtsId'

    val isDefault: Boolean,
    val createdAt: String? = null,
    val updatedAt: String? = null
)