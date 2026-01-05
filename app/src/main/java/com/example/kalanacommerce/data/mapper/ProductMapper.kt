package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.product.*
import com.example.kalanacommerce.domain.model.*

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        description = description ?: "",
        productCode = productCode ?: "-",
        image = image ?: "",
        stock = qty,
        price = cogs?.toLongOrNull() ?: 0L,
        tags = tags?.split(",")?.map { it.trim() } ?: emptyList(),
        isPublished = isPublished,
        variants = variants?.map { it.toDomain() } ?: emptyList(),
        outletName = outlet?.name ?: "Unknown Outlet",
        categoryName = category?.name ?: "Uncategorized",
        mainUnit = unit?.name ?: "Pcs" // Fallback unit
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
        location = "$latitude, $longitude",
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