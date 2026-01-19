package com.example.kalanacommerce.domain.model

data class Address(
    val id: String,
    val label: String,
    val recipientName: String,
    val phoneNumber: String,
    val street: String,
    val postalCode: String,
    val provincesId: String,
    val citiesId: String,
    val districtsId: String,
    val fullAddress: String,
    val isDefault: Boolean,
    val latitude: Double,
    val longitude: Double
)