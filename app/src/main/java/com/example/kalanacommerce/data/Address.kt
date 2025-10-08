package com.example.kalanacommerce.data

// Data/model/Address.kt

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Address(
    val id: Int? = null,
    val address_line: String,
    val city: String,
    val postal_code: String,
    val is_primary: Boolean,
    val created_at: String? = null
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AddressListResponse(
    val status: String,
    val total_addresses: Int,
    val addresses: List<Address>
)