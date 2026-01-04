package com.example.kalanacommerce.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val image: String?,
    val balance: String,
    // Field khusus untuk mempermudah passing token saat login
    val token: String? = null
)