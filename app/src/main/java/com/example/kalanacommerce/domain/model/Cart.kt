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
    val outletId: String,
    // Info Harga & Jumlah
    val price: Long,
    val quantity: Int,
    val stock: Int, // Sisa stok (dari variant)

    val discountPercentage: Int = 0, // Contoh: 15 (artinya 15%)
    val originalPrice: Long = 0,     // Harga sebelum diskon
    val freshness: Int = 100,        // Tingkat kesegaran (0-100)

    // Logic UI
    val maxQuantity: Int, // Dibatasi oleh stok
    val totalPrice: Long = price * quantity
)

data class CheckoutResult(
    val id: String, // Ini ID Order
    val orderCode: String,
    val totalAmount: Long,
    val snapToken: String,
    val snapRedirectUrl: String // Ini URL untuk WebView
)