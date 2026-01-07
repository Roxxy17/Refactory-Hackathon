package com.example.kalanacommerce.domain.usecase.chat

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.ChatMessage
import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.domain.repository.ChatRepository
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*

class SendMessageUseCase(
    private val chatRepository: ChatRepository,
    private val productRepository: ProductRepository
) {
    private val checkoutRegex = "<checkout>(.*?)</checkout>".toRegex(RegexOption.DOT_MATCHES_ALL)

    operator fun invoke(message: String): Flow<Resource<ChatMessage>> = flow {
        emit(Resource.Loading())

        // [FIX 1] Gunakan collect() alih-alih first().
        // Ini membiarkan flow repository selesai secara alami tanpa dipotong paksa (menghindari AbortFlowException).
        chatRepository.sendMessage(message).collect { result ->

            // Abaikan loading dari repository karena kita sudah emit Loading di awal
            if (result is Resource.Loading) return@collect

            if (result is Resource.Error) {
                emit(Resource.Error(result.message ?: "Gagal terhubung ke Chatbot"))
                return@collect
            }

            if (result is Resource.Success) {
                val rawResponse = result.data?.response ?: ""

                // Bersihkan teks untuk UI
                val cleanText = if (rawResponse.isBlank()) {
                    "Halo, ada yang bisa dibantu?"
                } else {
                    rawResponse.replace(checkoutRegex, "").trim()
                }

                val recommendations = mutableListOf<Product>()

                // Ekstrak ID Produk dari JSON
                val matchResult = checkoutRegex.find(rawResponse)
                val jsonString = matchResult?.groupValues?.get(1)

                if (!jsonString.isNullOrBlank()) {
                    val targetIds = mutableListOf<String>()

                    try {
                        val jsonElement = Json.parseToJsonElement(jsonString)
                        if (jsonElement is JsonArray) {
                            jsonElement.forEach { item ->
                                item.jsonObject["id"]?.jsonPrimitive?.content?.let {
                                    targetIds.add(it)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // [FIX 2] Parallel Fetching dengan coroutineScope + async
                    // Ini mengambil semua produk secara bersamaan, tidak saling tunggu.
                    if (targetIds.isNotEmpty()) {
                        val fetchedProducts = coroutineScope {
                            targetIds.map { id ->
                                async {
                                    // Helper function untuk mengambil produk dengan aman
                                    fetchProductSafely(id)
                                }
                            }.awaitAll() // Tunggu semua selesai
                        }

                        // Masukkan semua hasil yang sukses (tidak null)
                        recommendations.addAll(fetchedProducts.filterNotNull())
                    }
                }

                // Emit hasil akhir ke UI
                emit(Resource.Success(ChatMessage(cleanText, false, recommendations)))
            }
        }
    }.catch { e ->
        e.printStackTrace()
        emit(Resource.Error(e.message ?: "Terjadi kesalahan sistem"))
    }

    // Fungsi kecil untuk mengambil produk tanpa crash
    private suspend fun fetchProductSafely(id: String): Product? {
        var product: Product? = null
        try {
            // Gunakan collect, bukan first, untuk menghindari isu yang sama pada ProductRepository
            productRepository.getProductDetail(id).collect { res ->
                if (res is Resource.Success) {
                    product = res.data
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return product
    }
}