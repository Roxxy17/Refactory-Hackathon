package com.example.kalanacommerce.back

// com.example.kalanacommerce.data.model/Product.kt

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Product(
    val id: Int? = null,
    val store_id: Int? = null, // Dihilangkan dari respons GET publik
    val category_id: Int? = null, // Dihilangkan dari respons GET publik
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock_quantity: Int,
    val sku: String? = null,
    val is_active: Boolean? = true,
    val created_at: String? = null,

    // Field Tambahan dari JOIN Query (GET /products)
    val store_name: String? = null,
    val category_name: String? = null
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ProductListResponse(
    val status: String,
    val total_products: Int,
    val products: List<Product>
)

// Model untuk input POST (hanya field yang bisa diubah/dibuat)
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class NewProductRequest(
    val category_id: Int? = null,
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock_quantity: Int,
    val sku: String? = null
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ProductCreationResponse(
    val status: String,
    val message: String,
    val product: Product
)