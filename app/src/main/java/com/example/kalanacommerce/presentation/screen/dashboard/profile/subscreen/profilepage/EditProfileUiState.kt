package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val balance: String = "", // <--- TAMBAHKAN INI
    val profileImage: String? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)