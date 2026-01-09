package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.cart.CartItemDto
import com.example.kalanacommerce.data.remote.dto.cart.CheckoutResponseDto
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult

fun CartItemDto.toDomain(): CartItem {
    // Ambil data dari nested object 'variant'
    val variantData = variant
    val productData = variant?.product

    // Parsing harga dari String ke Long
    val priceLong = variantData?.price?.toLongOrNull() ?: 0L

    return CartItem(
        id = id,
        productVariantId = variantData?.id ?: "",
        productId = productData?.id ?: "",

        // Ambil nama & gambar dari dalam variant -> product
        productName = productData?.name ?: "Produk Tidak Dikenal",
        variantName = variantData?.variantName ?: "-",
        productImage = productData?.image ?: "",
        outletName = "Toko Kalana", // Default karena tidak ada info outlet di response cart item ini

        price = priceLong,
        quantity = quantity,
        stock = 100, // Default dummy stock
        maxQuantity = 100,

        totalPrice = priceLong * quantity
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