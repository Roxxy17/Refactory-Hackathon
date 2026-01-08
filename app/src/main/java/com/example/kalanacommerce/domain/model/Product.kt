package com.example.kalanacommerce.domain.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val productCode: String,
    val image: String,
    val stock: Int,

    // --- HARGA ---
    val price: Long, // Harga jual saat ini (setelah diskon)
    val originalPrice: Long? = null, // [BARU] Harga coret (sebelum diskon). Nullable jika tidak ada diskon.
// [TAMBAHKAN INI]
    val variantName: String = "", // Untuk menampung "1 Ikat (250gr)"
    // --- UI SPESIFIK ---
    val freshness: Int = 100, // [BARU] Persentase kesegaran (0-100)
    val weight: Int = 1, // [BARU] Angka berat (misal: 250)

    val tags: List<String>,
    val isPublished: Boolean,
    val variants: List<ProductVariant>,
    val outletName: String,
    val categoryName: String,
    val mainUnit: String, // Satuan (misal: "gr", "kg", "pcs")
    val outlet: Outlet? = null
) {
    // [HELPER] Hitung Persentase Diskon secara otomatis
    val discountPercentage: Int
        get() {
            if (originalPrice == null || originalPrice <= price) return 0
            return (((originalPrice - price).toDouble() / originalPrice) * 100).toInt()
        }
}

// Data class lain (ProductVariant, Outlet, dll) biarkan tetap sama...
data class ProductVariant(
    val id: String,
    val name: String,
    val price: Long,
    val originalPrice: Long,
    val unitName: String
)

data class Outlet(val id: String, val name: String, val location: String, val canPickup: Boolean, val canDelivery: Boolean)
data class Category(val id: String, val name: String)
data class MeasurementUnit(val id: String, val name: String)