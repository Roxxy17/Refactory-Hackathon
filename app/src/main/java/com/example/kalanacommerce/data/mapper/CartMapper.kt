package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.cart.CartItemDto
import com.example.kalanacommerce.data.remote.dto.cart.CheckoutResponseDto
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult
import kotlin.math.absoluteValue

fun CartItemDto.toDomain(): CartItem {
    val variantData = variant
    val productData = variant?.product

    val priceLong = variantData?.price?.toLongOrNull() ?: 0L

    // Seed untuk angka acak yang konsisten per produk
    val uniqueSeed = productData?.id.hashCode()?.absoluteValue ?: 0

    // A. Freshness (Mock Konsisten)
    val displayFreshness = 85 + (uniqueSeed % 16)

    // --- [MODIFIKASI: MOCKING 5 TOKO] ---
    // Kita buat daftar toko dummy
    val mockStores = listOf(
        "Toko Sayur Kalana" to "mock_store_001",
        "Mitra Tani Sejahtera" to "mock_store_002",
        "Segar Abadi Mart" to "mock_store_003",
        "Warung Bu Dewi" to "mock_store_004",
        "Kebun Organik Pak Budi" to "mock_store_005"
    )

    // Pilih toko berdasarkan ID Produk (supaya konsisten tidak berubah-ubah)
    val storeIndex = uniqueSeed % mockStores.size
    val (displayOutletName, displayOutletId) = mockStores[storeIndex]
    // ------------------------------------

    // Diskon: Semua barang diskon (sesuai request sebelumnya)
    val isDiscounted = true

    val displayOriginalPrice = if (isDiscounted) {
        // Markup harga 20% - 50%
        val markup = 1.2 + ((uniqueSeed % 4) / 10.0)
        (priceLong * markup).toLong()
    } else {
        priceLong
    }

    // Hitung Persentase Diskon
    val calculatedDiscount = if (displayOriginalPrice > priceLong) {
        try {
            ((displayOriginalPrice - priceLong).toDouble() / displayOriginalPrice * 100).toInt()
        } catch (e: Exception) { 0 }
    } else {
        0
    }

    // Logic Nama Varian
    val finalVariantName = if (!variantData?.variantName.isNullOrEmpty() && variantData?.variantName != "-") {
        variantData?.variantName ?: ""
    } else {
        variantData?.unit?.name ?: "Satuan"
    }

    return CartItem(
        id = id,
        productVariantId = variantData?.id ?: "",
        productId = productData?.id ?: "",
        productName = productData?.name ?: "Produk",
        productImage = productData?.image ?: "",

        variantName = finalVariantName,

        // Data Toko dari List Dummy (5 Toko)
        outletName = displayOutletName,
        outletId = displayOutletId,

        freshness = displayFreshness,
        originalPrice = displayOriginalPrice,
        discountPercentage = calculatedDiscount,

        price = priceLong,
        quantity = quantity,
        stock = 100,
        maxQuantity = 100,
        totalPrice = priceLong * quantity
    )
}

fun CheckoutResponseDto.toDomain(): CheckoutResult {
    return CheckoutResult(
        id = id,
        orderCode = orderCode,
        totalAmount = totalAmount,
        snapToken = snapToken ?: "",
        // 👇 Pastikan ini mengambil dari field URL, bukan Token
        snapRedirectUrl = snapRedirectUrl ?: ""
    )
}