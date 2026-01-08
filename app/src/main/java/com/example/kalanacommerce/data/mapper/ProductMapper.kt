package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.product.*
import com.example.kalanacommerce.domain.model.*

fun ProductDto.toDomain(): Product {
    // 1. Ambil varian pertama sebagai acuan tampilan di Card (List Produk)
    val defaultVariant = variants?.firstOrNull()

    // 2. Prioritaskan harga dari Varian. Jika tidak ada, baru fallback ke COGS atau 0
    val displayPrice = defaultVariant?.price?.toLongOrNull() ?: cogs?.toLongOrNull() ?: 0L
    val displayOriginalPrice = defaultVariant?.originalPrice?.toLongOrNull()

    // 3. Ambil berat dari qtyMultiplier (misal: 200), default 1
    val displayWeight = defaultVariant?.qtyMultiplier ?: 1

    // 4. Ambil nama unit dari varian (misal: "Ikat" atau "Gram"), fallback ke unit utama
    val displayUnit = defaultVariant?.unit?.name ?: unit?.name ?: "Pcs"

    return Product(
        id = id,
        name = name,
        description = description ?: "",
        productCode = productCode ?: "-",
        image = image ?: "",
        stock = qty,

        // --- DATA YANGIPERBAIKI ---
        price = displayPrice,            // Harga Jual (6500)
        originalPrice = displayOriginalPrice, // Harga Coret (7500)
        variantName = defaultVariant?.variantName ?: "-",
        // PENTING: Mapping Freshness (API: freshnessLevel -> Domain: freshness)
        freshness = freshnessLevel ?: 100,

        // PENTING: Mapping Berat & Satuan
        weight = displayWeight,          // 200
        mainUnit = displayUnit,          // "Gram" atau "Ikat"

        tags = tags?.split(",")?.map { it.trim() } ?: emptyList(),
        isPublished = isPublished,
        variants = variants?.map { it.toDomain() } ?: emptyList(),
        outletName = outlet?.name ?: "Unknown Outlet",
        categoryName = category?.name ?: "Uncategorized",
        outlet = outlet?.toDomain()
    )
}

fun ProductVariantDto.toDomain(): ProductVariant {
    return ProductVariant(
        id = id,
        name = variantName,
        price = price.toLongOrNull() ?: 0L,
        originalPrice = originalPrice?.toLongOrNull() ?: 0L,
        unitName = unit?.name ?: ""
    )
}

fun OutletDto.toDomain(): Outlet {
    return Outlet(
        id = id,
        name = name,
        location = "${latitude ?: "0.0"}, ${longitude ?: "0.0"}",
        canPickup = settings?.pickup ?: false,
        canDelivery = settings?.delivery ?: false
    )
}

fun CategoryDto.toDomain(): Category {
    return Category(
        id = id,
        name = name
    )
}

fun UnitDto.toDomain(): MeasurementUnit {
    return MeasurementUnit(
        id = id,
        name = name
    )
}