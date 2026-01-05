package com.example.kalanacommerce.domain.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val productCode: String,
    val image: String,
    val stock: Int,
    val price: Long, // Harga dasar (COGS) atau harga display
    val tags: List<String>,
    val isPublished: Boolean,
    val variants: List<ProductVariant>,
    val outletName: String,
    val categoryName: String,
    val mainUnit: String
)

data class ProductVariant(
    val id: String,
    val name: String,
    val price: Long,
    val originalPrice: Long,
    val unitName: String
)

data class Outlet(
    val id: String,
    val name: String,
    val location: String, // Gabungan lat, long atau nama kota
    val canPickup: Boolean,
    val canDelivery: Boolean
)

data class Category(
    val id: String,
    val name: String
)

data class MeasurementUnit( // Hindari nama 'Unit' karena konflik dengan Kotlin Unit
    val id: String,
    val name: String
)