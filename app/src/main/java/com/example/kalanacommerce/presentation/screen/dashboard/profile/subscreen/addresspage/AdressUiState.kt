package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage

import com.example.kalanacommerce.domain.model.Address

data class AddressUiState(
    val isLoading: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val selectedAddress: Address? = null, // <--- Field ini wajib ada untuk Form
    val error: String? = null,
    val successMessage: String? = null
)