package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.order.OrderDto
import com.example.kalanacommerce.data.remote.dto.order.OrderItemDto
import com.example.kalanacommerce.domain.model.Order
import com.example.kalanacommerce.domain.model.OrderItem
import com.example.kalanacommerce.domain.model.OrderStatus

fun OrderDto.toDomain(): Order {
    return Order(
        id = id,
        orderCode = orderCode,
        outletName = outlet?.name ?: "Toko Kalana",
        totalAmount = totalAmount,
        status = parseStatus(status),
        paymentMethod = paymentMethod?.replace("_", " ") ?: "Belum Dipilih", // Biar rapi (MIDTRANS_QRIS -> MIDTRANS QRIS)
        date = createdAt,
        snapToken = snapToken,
        snapRedirectUrl = snapRedirectUrl,
        paymentGroupId = paymentGroupId,
        itemCount = _count?.items ?: items?.size ?: 0,
        items = items?.map { it.toDomain() } ?: emptyList()
    )
}

fun OrderItemDto.toDomain(): OrderItem {
    // [FIX] Konversi String ke Long
    val priceLong = price.toLongOrNull() ?: 0L
    val totalLong = totalPrice.toLongOrNull() ?: 0L

    return OrderItem(
        id = id,
        // [FIX] Ambil nama & gambar dari nested variant -> product
        productName = variant?.product?.name ?: "Produk Tidak Dikenal",
        variantName = variant?.variantName ?: "-",
        image = variant?.product?.image ?: "",

        quantity = quantity,
        price = priceLong,
        totalPrice = totalLong
    )
}

fun parseStatus(status: String): OrderStatus {
    return try {
        OrderStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        OrderStatus.UNKNOWN
    }
}