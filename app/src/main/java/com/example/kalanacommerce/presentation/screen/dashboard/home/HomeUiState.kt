package com.example.kalanacommerce.presentation.screen.dashboard.home

import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.model.Product

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val products: List<Product> = emptyList(), // Semua produk
    val displayProducts: List<Product> = emptyList(), // Produk yg ditampilkan (terfilter)
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String = "ALL", // "ALL" atau ID kategori
    val searchQuery: String = ""
)