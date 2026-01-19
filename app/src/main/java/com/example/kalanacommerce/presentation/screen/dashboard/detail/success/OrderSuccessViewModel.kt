package com.example.kalanacommerce.presentation.screen.dashboard.detail.success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.AddressRepository
import com.example.kalanacommerce.domain.repository.ProductRepository
import com.example.kalanacommerce.domain.usecase.order.GetOrdersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class OrderSuccessViewModel(
    private val addressRepository: AddressRepository,
    private val getOrdersUseCase: GetOrdersUseCase,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderSuccessUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData(orderId: String?, groupId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. AMBIL TITIK AWAL: Alamat Utama User
            var userGeo: GeoPoint? = null
            addressRepository.getAddresses().collect { result ->
                if (result is Resource.Success) {
                    // Cari alamat default, atau fallback ke alamat pertama
                    val targetAddr = result.data?.find { it.isDefault } ?: result.data?.firstOrNull()

                    if (targetAddr != null) {
                        // [FIX] Gunakan data latitude/longitude asli dari objek Address
                        // Hapus kode hardcode/dummy sebelumnya
                        if (targetAddr.latitude != 0.0 && targetAddr.longitude != 0.0) {
                            userGeo = GeoPoint(targetAddr.latitude, targetAddr.longitude)
                        }
                    }
                }
            }

            // 2. AMBIL TITIK TUJUAN: Lokasi Outlet dari Order
            val targetOutletIds = mutableListOf<String>()

            getOrdersUseCase().collect { result ->
                if (result is Resource.Success) {
                    val orders = result.data ?: emptyList()
                    val relevantOrders = if (groupId != null) {
                        orders.filter { it.paymentGroupId == groupId }
                    } else {
                        orders.filter { it.id == orderId }
                    }
                    targetOutletIds.addAll(relevantOrders.map { it.outletId }.distinct())
                }
            }

            // 3. Ambil Koordinat Toko
            val storeGeos = mutableListOf<GeoPoint>()
            if (targetOutletIds.isNotEmpty()) {
                productRepository.getOutlets().collect { result ->
                    if (result is Resource.Success) {
                        val allOutlets = result.data ?: emptyList()
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
                    }
                }
            }

            // Update UI
            _uiState.update {
                it.copy(
                    isLoading = false,
                    // Jika userGeo masih null (misal user belum punya alamat), fallback ke default (misal Jogja)
                    userLocation = userGeo ?: GeoPoint(-7.77, 110.37),
                    storeLocations = storeGeos
                )
            }
        }
    }
}