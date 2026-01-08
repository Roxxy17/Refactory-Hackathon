package com.example.kalanacommerce.presentation.screen.dashboard.store

import com.example.kalanacommerce.domain.model.Outlet
import com.example.kalanacommerce.domain.model.Product

data class DetailStoreUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val outlet: Outlet? = null,
    val outletProducts: List<Product> = emptyList(), // Produk khusus toko ini
    val selectedCategoryFilter: String = "Semua" // Filter kategori di dalam toko
)