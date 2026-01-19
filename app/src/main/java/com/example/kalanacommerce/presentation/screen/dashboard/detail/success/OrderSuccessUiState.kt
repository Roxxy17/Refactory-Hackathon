package com.example.kalanacommerce.presentation.screen.dashboard.detail.success

import org.osmdroid.util.GeoPoint

data class OrderSuccessUiState(
    val isLoading: Boolean = true,
    val userLocation: GeoPoint? = null,
    val storeLocations: List<GeoPoint> = emptyList(),
    val error: String? = null
)