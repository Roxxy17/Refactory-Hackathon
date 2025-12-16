package com.example.kalanacommerce.data.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

// --- 1. MODEL UNTUK REQUEST CHECKOUT (POST /orders/orders) ---

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class OrderItemRequest(
    val product_id: Int,
    val quantity: Int
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class NewOrderRequest(
    // Daftar item yang akan dibeli
    val items: List<OrderItemRequest>,
    // ID alamat yang dipilih dari tabel 'addresses'
    val address_id: Int,
    // Penyedia pengiriman (shipping_provider)
    val shipping_provider: String? = null
)

// --- 2. MODEL UNTUK RESPONSE (GET & POST) ---

// Model ini merefleksikan data yang dikembalikan oleh kueri RETURNING di router Anda.
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class OrderResponse(
    val id: String, // Misal: ORD-F9E2A8C1
    val total_amount: Double,
    val status: String, // Menggunakan String untuk OrderStatus Enum (pending, paid, delivered, etc.)
    val created_at: String
)

// Model untuk Response Sukses saat Checkout (POST)
// Router Anda mengembalikan { status: "Sukses", message: "...", order: {...} }
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class OrderCreationResponse(
    val status: String,
    val message: String,
    val order: OrderResponse
)

// Model untuk Response Daftar Pesanan (GET)
// Router Anda mengembalikan { status: "Sukses", total_orders: X, orders: [...] }
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class OrderListResponse(
    val status: String,
    val total_orders: Int,
    val orders: List<OrderResponse>
)

// Model Error Global (Asumsi untuk parsing error dari API)
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ServerErrorResponse(
    val status: String,
    val error: String? = null
)