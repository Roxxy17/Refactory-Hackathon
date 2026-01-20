package com.example.kalanacommerce.presentation.screen.dashboard.detail.success

import com.example.kalanacommerce.domain.model.Order
import org.osmdroid.util.GeoPoint

data class OrderSuccessUiState(
    val isLoading: Boolean = true,
    val userLocation: GeoPoint? = null,
    val storeLocations: List<GeoPoint> = emptyList(),

    // [BARU] Menyimpan daftar order lengkap untuk dikontrol di UI
    val orders: List<Order> = emptyList(),

    val error: String? = null
) {
    // Helper: Cek apakah SEMUA pesanan sudah selesai (PICKED_UP)
    val isAllOrdersCompleted: Boolean
        get() = orders.isNotEmpty() && orders.all { it.pickupStatus == "PICKED_UP" }
}