package com.example.kalanacommerce.presentation.screen.dashboard.detail.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult
import com.example.kalanacommerce.domain.repository.AddressRepository
import com.example.kalanacommerce.domain.repository.ProductRepository
import com.example.kalanacommerce.domain.usecase.cart.CheckoutUseCase
import com.example.kalanacommerce.domain.usecase.cart.DirectCheckoutUseCase
import com.example.kalanacommerce.domain.usecase.cart.GetCartItemsUseCase
import com.example.kalanacommerce.domain.usecase.cart.UpdateCartItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CheckoutViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val directCheckoutUseCase: DirectCheckoutUseCase,
    private val productRepository: ProductRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    private var isDirectCheckout: Boolean = false
    private var directPayload: Pair<String, Int>? = null

    private val _timelineState = MutableStateFlow(PickupTimelineState())
    val timelineState = _timelineState.asStateFlow()

    private val _storeLocationState = MutableStateFlow(
        StoreLocationModel("Memuat Toko...", "-", "-")
    )
    val storeLocationState = _storeLocationState.asStateFlow()

    fun loadCheckoutItems(payload: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadAddresses()

            if (payload.startsWith("DIRECT__")) {
                isDirectCheckout = true
                val parts = payload.split("__")
                if (parts.size == 3) {
                    val variantId = parts[1]
                    val qty = parts[2].toIntOrNull() ?: 1
                    directPayload = Pair(variantId, qty)
                    loadDirectProductInfo(variantId, qty)
                }
            } else {
                isDirectCheckout = false
                val itemIds = payload.split(",").filter { it.isNotEmpty() }
                loadCartItems(itemIds)
            }
        }
    }

    // --- LOGIC CART CHECKOUT ---

    private fun loadCartItems(itemIds: List<String>) {
        viewModelScope.launch {
            getCartItemsUseCase().collect { result ->
                if (result is Resource.Success) {
                    val allItems = result.data ?: emptyList()
                    // Filter item yang dipilih user
                    val selectedItems = allItems.filter { it.id in itemIds }
                    val sortedItems = selectedItems.sortedBy { it.outletId }

                    _uiState.update { it.copy(isLoading = false, checkoutItems = sortedItems) }
                    recalculateLogistics(sortedItems)
                }
            }
        }
    }

    // [PERBAIKAN ERROR 7] Menambahkan fungsi updateQuantity
    fun updateQuantity(itemId: String, newQty: Int) {
        if (isDirectCheckout) {
            // Direct checkout biasanya statis, tapi jika ingin diubah:
            val currentItems = _uiState.value.checkoutItems.toMutableList()
            val index = currentItems.indexOfFirst { it.id == itemId }
            if (index != -1) {
                val newItem = currentItems[index].copy(quantity = newQty, totalPrice = currentItems[index].price * newQty)
                currentItems[index] = newItem
                _uiState.update { it.copy(checkoutItems = currentItems) }
                // Update juga payload untuk direct checkout
                directPayload = directPayload?.copy(second = newQty)
            }
        } else {
            // Cart checkout: Update ke server lalu reload
            viewModelScope.launch {
                updateCartItemUseCase(itemId, newQty).collect { result ->
                    if (result is Resource.Success) {
                        // Reload items agar harga & total sinkron
                        val currentIds = _uiState.value.checkoutItems.map { it.id }
                        loadCartItems(currentIds)
                    }
                }
            }
        }
    }

    // [PERBAIKAN ERROR 8] Menambahkan fungsi selectAddress
    fun selectAddress(address: AddressUiModel) {
        _uiState.update { it.copy(selectedAddress = address) }
    }

    // --- LOGIC DIRECT CHECKOUT ---

    private fun loadDirectProductInfo(variantId: String, qty: Int) {
        viewModelScope.launch {
            productRepository.getProducts("").collect { result ->
                if (result is Resource.Success) {
                    val allProducts = result.data ?: emptyList()
                    val product = allProducts.find { p -> p.id == variantId || p.variants.any { v -> v.id == variantId } }

                    if (product != null) {
                        val variant = product.variants.find { it.id == variantId }

                        val dummyCartItem = CartItem(
                            id = "TEMP_DIRECT",
                            productId = product.id,
                            productName = product.name,
                            productImage = product.image,
                            productVariantId = variantId,
                            variantName = variant?.name ?: product.variantName,
                            price = variant?.price ?: product.price,
                            quantity = qty,
                            outletName = "Toko Kalana (Langsung)",
                            outletId = "direct_store_id",
                            stock = 999,
                            maxQuantity = 999
                        )

                        val items = listOf(dummyCartItem)
                        _uiState.update { it.copy(isLoading = false, checkoutItems = items) }
                        recalculateLogistics(items)
                    }
                }
            }
        }
    }

    // --- LOGIC LOGISTIK ---

    private fun recalculateLogistics(items: List<CartItem>) {
        if (items.isEmpty()) return

        val uniqueStoreNames = items.map { it.outletName }.distinct()
        val storeCount = uniqueStoreNames.size

        val locationModel = if (storeCount <= 1) {
            val storeName = uniqueStoreNames.firstOrNull() ?: "Toko Kalana"
            StoreLocationModel(
                name = storeName,
                address = "Lokasi Penjual Terverifikasi",
                distance = "${Random.nextInt(1, 5)} km",
                isMultiStore = false
            )
        } else {
            val combinedNames = uniqueStoreNames.take(2).joinToString(", ") + if(storeCount > 2) "..." else ""
            StoreLocationModel(
                name = "$storeCount Toko ($combinedNames)",
                address = "Dikirim dari berbagai titik",
                distance = "${storeCount * 2} km (Estimasi)",
                isMultiStore = true
            )
        }
        _storeLocationState.value = locationModel

        val basePrep = 10
        val baseTravel = 15
        val extraTimePerStore = (storeCount - 1) * 5

        val totalPrep = basePrep + extraTimePerStore
        val totalTravel = baseTravel + extraTimePerStore
        val totalPickup = 2 * storeCount

        _timelineState.update {
            PickupTimelineState(
                prepTime = totalPrep,
                travelTime = totalTravel,
                pickupTime = totalPickup,
                totalTime = totalPrep + totalTravel + totalPickup,
                storeCount = storeCount
            )
        }
    }

    fun placeOrder() {
        if (_uiState.value.checkoutItems.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            if (isDirectCheckout && directPayload != null) {
                directCheckoutUseCase(directPayload!!.first, directPayload!!.second).collect { handleCheckoutResult(it) }
            } else {
                val itemIds = _uiState.value.checkoutItems.map { it.id }
                checkoutUseCase(itemIds).collect { handleCheckoutResult(it) }
            }
        }
    }

    private fun handleCheckoutResult(result: Resource<List<CheckoutResult>>) {
        when (result) {
            is Resource.Success -> _uiState.update { it.copy(isLoading = false, checkoutResult = result.data?.firstOrNull()) }
            is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            is Resource.Loading -> {}
        }
    }

    fun loadAddresses() {
        viewModelScope.launch {
            addressRepository.getAddresses().collect { result ->
                if (result is Resource.Success) {
                    val rawAddresses = result.data ?: emptyList()
                    val uiAddresses = rawAddresses.map {
                        AddressUiModel(it.id, it.recipientName, it.phoneNumber, it.fullAddress, it.isDefault, it.label)
                    }
                    val currentSelected = _uiState.value.selectedAddress
                    val defaultAddr = uiAddresses.find { it.isMain } ?: uiAddresses.firstOrNull()
                    _uiState.update { it.copy(availableAddresses = uiAddresses, selectedAddress = currentSelected ?: defaultAddr) }
                }
            }
        }
    }

    fun onPaymentNavigationHandled() {
        _uiState.update { it.copy(checkoutResult = null) }
    }
}