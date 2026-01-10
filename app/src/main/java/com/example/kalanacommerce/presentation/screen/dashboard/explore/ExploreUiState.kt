// ExploreUiState.kt
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.model.Product

data class ExploreUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: Category? = null, // [BARU] Menyimpan kategori yang sedang dipilih
    val categories: List<Category> = emptyList(),
    val searchResults: List<Product> = emptyList(),
    val navigateToCheckoutWithId: String? = null,
    val successMessage: String? = null
)

// Buat Data Class Wrapper untuk UI
data class UiCategory(
    val id: String,
    val nameResId: Int, // Gunakan Resource ID, bukan String
    val apiQuery: String,
    val validationKeywords: List<String> // List kata kunci untuk filter
)