package com.example.kalanacommerce.core.util

// Sealed class ini berfungsi seperti "Amplop Surat"
// Amplop ini bisa berisi 3 jenis surat: Loading, Sukses (ada isinya), atau Error (ada pesan errornya)
sealed class Resource<T>(val data: T? = null, val message: String? = null) {

    // 1. Amplop Sukses: Pasti membawa data
    class Success<T>(data: T) : Resource<T>(data)

    // 2. Amplop Error: Membawa pesan error (data opsional)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    // 3. Amplop Loading: Memberi tahu UI untuk muter-muter (loading)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}