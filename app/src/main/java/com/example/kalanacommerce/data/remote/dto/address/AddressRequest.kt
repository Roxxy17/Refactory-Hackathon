package com.example.kalanacommerce.data.remote.dto.address

import kotlinx.serialization.Serializable

@Serializable
data class AddressRequest(
    val label: String,
    val recipientName: String,
    val phoneNumber: String,
    val street: String,
    val postalCode: String,
    val provincesId: String,
    val citiesId: String,
    val districtsId: String,
    val isDefault: Boolean,
    val long: Double = 0.0,
    val lat: Double = 0.0
)