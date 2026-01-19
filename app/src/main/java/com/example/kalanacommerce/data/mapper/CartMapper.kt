package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.cart.CartItemDto
import com.example.kalanacommerce.data.remote.dto.cart.CheckoutResponseDto
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult

fun CartItemDto.toDomain(): CartItem {
    val variantData = this.variant
    val productData = variantData.product
    val priceLong = variantData.price.toLongOrNull() ?: 0L
    val originalPriceLong = variantData.originalPrice?.toLongOrNull()
    val finalVariantName = if (!variantData.variantName.isNullOrEmpty() && variantData.variantName != "-") {
        variantData.variantName
    } else {
        variantData.unit?.name ?: "Satuan"
    }
    return CartItem(
        id = this.id,
        productVariantId = variantData.id,
        productId = productData.id,
        productName = productData.name,
        productImage = productData.image ?: "",
        variantName = finalVariantName,
        outletName = productData.outlet.name,
        outletId = productData.outlet.id,
        freshness = productData.freshnessLevel ?: 100,
        originalPrice = originalPriceLong,
        price = priceLong,
        quantity = this.quantity,
        stock = 100,
        maxQuantity = 100,
        totalPrice = priceLong * this.quantity
    )
}

fun CheckoutResponseDto.toDomain(): CheckoutResult {
    return CheckoutResult(
        id = id,
        orderCode = orderCode,
        totalAmount = totalAmount,
        snapToken = snapToken ?: "",
        snapRedirectUrl = snapRedirectUrl ?: "",
        paymentGroupId = paymentGroupId ?: ""
    )
}