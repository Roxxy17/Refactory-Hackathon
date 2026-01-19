package com.example.kalanacommerce.domain.model

data class CartItem(
    val id: String,
    val productVariantId: String,
    val productId: String,

    // Info Tampilan Produk
    val productName: String,
    val variantName: String,
    val productImage: String,

    // [BARU] Info Toko Asli (Penting untuk Grouping)
    val outletName: String,
    val outletId: String,

    // Info Harga & Jumlah
    val price: Long,
    val quantity: Int,
    val stock: Int = 100, // Default 100 (karena belum ada di response API Cart)

    // [BARU] Info Logic Asli
    val originalPrice: Long? = null, // Harga coret
    val freshness: Int = 100,        // Persentase kesegaran

    // Logic UI
    val maxQuantity: Int = 100,
    val totalPrice: Long = price * quantity
) {
    // Helper untuk cek diskon di UI
    val hasDiscount: Boolean
        get() = originalPrice != null && originalPrice > price

    // Helper hitung persen diskon
    val discountPercentage: Int
        get() {
            if (originalPrice == null || originalPrice <= price) return 0
            return (((originalPrice - price).toDouble() / originalPrice) * 100).toInt()
        }
}

// Model untuk hasil Checkout (Snap Token)
data class CheckoutResult(
    val id: String,
    val orderCode: String,
    val totalAmount: Long,
    val snapToken: String,
    val snapRedirectUrl: String,
    val paymentGroupId: String? = null
)