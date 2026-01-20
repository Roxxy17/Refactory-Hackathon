// File: presentation/screen/dashboard/detail/success/OrderSuccessViewModel.kt
package com.example.kalanacommerce.presentation.screen.dashboard.detail.success

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.AddressRepository
import com.example.kalanacommerce.domain.repository.ProductRepository
import com.example.kalanacommerce.domain.usecase.order.GetOrdersUseCase
import com.example.kalanacommerce.domain.usecase.order.UpdatePickupStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class OrderSuccessViewModel(
    private val addressRepository: AddressRepository,
    private val getOrdersUseCase: GetOrdersUseCase,
    private val productRepository: ProductRepository,
    private val updatePickupStatusUseCase: UpdatePickupStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderSuccessUiState())
    val uiState = _uiState.asStateFlow()

    // Fungsi Update Status (Tidak berubah)
    fun updateOrderPickupStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            updatePickupStatusUseCase(orderId, newStatus).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        _uiState.update { currentState ->
                            val updatedList = currentState.orders.map { order ->
                                if (order.id == orderId) result.data ?: order.copy(pickupStatus = newStatus) else order
                            }
                            currentState.copy(orders = updatedList)
                        }
                    }
                    is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun loadData(orderId: String?, groupId: String?) {
        _uiState.update { it.copy(isLoading = true) }

        // 1. Jalankan Alamat User secara Paralel (agar tidak saling tunggu)
        viewModelScope.launch {
            addressRepository.getAddresses().collect { result ->
                if (result is Resource.Success) {
                    val targetAddr = result.data?.find { it.isDefault } ?: result.data?.firstOrNull()
                    if (targetAddr != null && targetAddr.latitude != 0.0) {
                        _uiState.update {
                            it.copy(userLocation = GeoPoint(targetAddr.latitude, targetAddr.longitude))
                        }
                    }
                }
            }
        }

        // 2. Jalankan Logika Order & Toko (Dependent Logic)
        viewModelScope.launch {
            getOrdersUseCase().collect { result ->
                if (result is Resource.Success) {
                    val allOrders = result.data ?: emptyList()

                    // Filter Order
                    val relevantOrders = if (groupId != null) {
                        allOrders.filter { it.paymentGroupId == groupId }
                    } else {
                        allOrders.filter { it.id == orderId }
                    }

                    // Ambil ID Toko Unik
                    val targetOutletIds = relevantOrders.map { it.outletId }.distinct()

                    // [PERBAIKAN UTAMA] Fetch Outlet DI DALAM sini, jangan di luar
                    if (targetOutletIds.isNotEmpty()) {
                        productRepository.getOutlets().collect { outletResult ->
                            if (outletResult is Resource.Success) {
                                val allOutlets = outletResult.data ?: emptyList()
                                val storeGeos = mutableListOf<GeoPoint>()

                                targetOutletIds.forEach { id ->
                                    val outlet = allOutlets.find { it.id == id }
                                    if (outlet != null) {
                                        val lat = outlet.lat?.toDoubleOrNull() ?: 0.0
                                        val long = outlet.long?.toDoubleOrNull() ?: 0.0
                                        if (lat != 0.0 && long != 0.0) {
                                            storeGeos.add(GeoPoint(lat, long))
                                        }
                                    }
                                }

                                // Update State Lengkap (Order + Lokasi Toko)
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        orders = relevantOrders,
                                        storeLocations = storeGeos // Lokasi toko terisi
                                    )
                                }
                            }
                        }
                    } else {
                        // Case jika tidak ada toko (jarang terjadi)
                        _uiState.update { it.copy(isLoading = false, orders = relevantOrders) }
                    }
                }
            }
        }
    }
}