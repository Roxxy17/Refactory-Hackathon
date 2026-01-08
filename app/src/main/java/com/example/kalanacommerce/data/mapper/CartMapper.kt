package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.cart.CartItemDto
import com.example.kalanacommerce.data.remote.dto.cart.CheckoutResponseDto
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult

fun CartItemDto.toDomain(): CartItem {
    val currentPrice = variant?.price?.toLongOrNull() ?: 0L

    return CartItem(
        id = id,
        productVariantId = productVariantId,
        productId = product?.id ?: "",

        productName = product?.name ?: "Unknown Product",
        variantName = variant?.variantName ?: "-",
        productImage = product?.image ?: "",
        outletName = product?.outlet?.name ?: "Unknown Store",

        price = currentPrice,
        quantity = quantity,
        // Ambil stok dari variant jika ada, default 0
        stock = 100, // TODO: Mapping stok real dari API jika tersedia di VariantDto
        maxQuantity = 100, // Sementara hardcode atau ambil dari stok

        totalPrice = currentPrice * quantity
    )
}

fun CheckoutResponseDto.toDomain(): CheckoutResult {
    return CheckoutResult(
        orderCode = orderCode,
        totalAmount = totalAmount,
        snapToken = snapToken ?: "",
        redirectUrl = snapRedirectUrl ?: ""
    )
}