package com.example.kalanacommerce.domain.model

data class Order(
    val id: String,
    val orderCode: String,
    val outletName: String,
    val totalAmount: Long,
    val status: OrderStatus, // Enum biar aman
    val paymentMethod: String,
    val date: String,
    val snapToken: String?,
    val snapRedirectUrl: String?,
    val itemCount: Int,
    val redirectUrl: String = "",
    val paymentGroupId: String? = null,

    // Untuk Detail
    val items: List<OrderItem> = emptyList()

)

data class OrderItem(
    val id: String,
    val productName: String,
    val variantName: String,
    val image: String,
    val quantity: Int,
    val price: Long,
    val totalPrice: Long
)


enum class OrderStatus(val label: String) {
    PENDING("Menunggu Pembayaran"),
    PAID("Dibayar"),
    PROCESSED("Diproses"),
    SHIPPED("Dikirim"),
    COMPLETED("Selesai"),
    CANCELLED("Dibatalkan"),
    UNKNOWN("Status Tidak Diketahui"),

    // [TAMBAHAN BARU] Wajib ada untuk handle respon Midtrans
    FAILED("Pembayaran Gagal"),
    EXPIRED("Waktu Pembayaran Habis")
}