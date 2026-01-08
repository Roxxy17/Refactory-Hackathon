package com.example.kalanacommerce.domain.model

data class CartItem(
    val id: String,
    val productVariantId: String,
    val productId: String,

    // Info Tampilan
    val productName: String,
    val variantName: String,
    val productImage: String,
    val outletName: String,

    // Info Harga & Jumlah
    val price: Long,
    val quantity: Int,
    val stock: Int, // Sisa stok (dari variant)

    // Logic UI
    val maxQuantity: Int, // Dibatasi oleh stok
    val totalPrice: Long = price * quantity
)

data class CheckoutResult(
    val orderCode: String,
    val totalAmount: Long,
    val snapToken: String,
    val redirectUrl: String
)