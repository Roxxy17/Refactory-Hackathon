package com.example.kalanacommerce.data.remote.dto.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val status: Boolean,
    val message: String,

    // [PENTING] Tambahkan '? = null' agar field ini TIDAK WAJIB ada di JSON
    val statusCode: Int? = null,

    val data: T? = null
)
// --- PRODUCT DTO ---
@Serializable
data class ProductDto(
    val id: String,
    val outletId: String,
    // Berikan default value = null untuk semua field nullable
    val categoryId: String? = null,
    val unitId: String? = null,
    val productCode: String? = null,
    val name: String,
    val description: String? = null,

    val image: String? = null, // <--- PENTING: Tambahkan = null

    val qty: Int,
    val cogs: String? = null,
    val freshnessLevel: Int? = null,
    val isPublished: Boolean,
    val tags: String? = null,
    val variants: List<ProductVariantDto>? = null,
    val outlet: OutletDto? = null,
    val category: CategoryDto? = null,
    val unit: UnitDto? = null
)

@Serializable
data class ProductVariantDto(
    val id: String,
    val productId: String,
    val unitId: String,
    val variantName: String,
    val price: String, // String dari API ("6500")
    val originalPrice: String?,
    val qtyMultiplier: Int?,
    val unit: UnitDto?
)

// --- OUTLET DTO ---
@Serializable
data class OutletDto(
    val id: String,
    val name: String,
    val provincesId: String?,
    val citiesId: String?,
    val districtsId: String?,
    @SerialName("long") val longitude: String?,
    @SerialName("lat") val latitude: String?,
    val settings: OutletSettingsDto?
)

@Serializable
data class OutletSettingsDto(
    val pickup: Boolean,
    val delivery: Boolean
)

// --- CATEGORY DTO ---
@Serializable
data class CategoryDto(
    val id: String,
    val name: String
)

// --- UNIT DTO ---
@Serializable
data class UnitDto(
    val id: String,
    val name: String,
    val outletId: String? = null
)