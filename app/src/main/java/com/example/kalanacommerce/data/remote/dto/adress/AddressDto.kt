package com.example.kalanacommerce.data.remote.dto.adress

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val id: String,
    val userId: String,
    val label: String,        // "Rumah Utama", dll
    val recipientName: String,
    val phoneNumber: String,
    val street: String,
    val postalCode: String,
    val provincesId: String,
    val citiesId: String,
    val isDefault: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String? = null
)