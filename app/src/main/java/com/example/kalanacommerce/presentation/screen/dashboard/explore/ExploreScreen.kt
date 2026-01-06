package com.example.kalanacommerce.presentation.screen.dashboard.explore

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.presentation.screen.dashboard.home.ProductCardItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = koinViewModel(),
    onBackClick: () -> Unit // [BARU] Parameter untuk navigasi back
) {
    val uiState by viewModel.uiState.collectAsState()

    // [BARU] Handle Tombol Back Native (Hardware/Gesture)
    BackHandler(enabled = true) {
        if (uiState.searchQuery.isNotEmpty()) {
            // Jika sedang mencari, back akan membersihkan search dulu
            viewModel.onSearchQueryChange("")
        } else {
            // Jika tidak sedang mencari, jalankan navigasi back normal
            onBackClick()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ExploreHeader(
                searchQuery = uiState.searchQuery,
                onSearchChange = viewModel::onSearchQueryChange
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // Jika User sedang mencari, tampilkan Hasil Search
            if (uiState.searchQuery.isNotEmpty()) {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    ProductSearchResults(products = uiState.searchResults)
                }
            }
            // Tampilan Awal (Default Explore)
            else {
                // 1. Quick Filters (Chips)
                QuickFilterRow()

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Banner Promo (Static)
                ExploreBanner()

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Grid Kategori (3 Kolom)
                Text(
                    text = "Kategori",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                CategoryGrid(categories = uiState.categories)
            }
        }
    }
}

// --- HEADER SEARCH ---
@Composable
fun ExploreHeader(searchQuery: String, onSearchChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Field
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Cari sayur, buah...", fontSize = 14.sp, color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Color.Gray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, null, tint = Color.Gray)
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.weight(1f).height(50.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Cart Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { /* Go to Cart */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.ShoppingCart, null, tint = Color.Black)
        }
    }
}

// --- QUICK FILTERS (CHIPS) ---
@Composable
fun QuickFilterRow() {
    val filters = listOf("Sayur Segar", "Buah", "Ayam", "Tomat", "Selada", "Paket Masak")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon X kecil untuk simulasi filter aktif (sesuai gambar referensi)
                    // Jika mau dinamis, bisa diubah logikanya
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = filter, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                }
            }
        }
    }
}

// --- BANNER PROMO ---
@Composable
fun ExploreBanner() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.slide_1),
            contentDescription = "Promo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// --- GRID KATEGORI (3 Kolom) ---
@Composable
fun CategoryGrid(categories: List<Category>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 Kolom sesuai desain
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCard(category)
        }
    }
}

@Composable
fun CategoryCard(category: Category) {
    // Ambil gambar berdasarkan nama kategori (karena API tidak menyediakan URL gambar)
    val imageRes = getCategoryImage(category.name)
    val isPopular = category.name.contains("Paket", true) // Contoh logika label

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().aspectRatio(0.8f).clickable { /* Navigate to Category */ }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(8.dp)
            ) {
                // Gambar Kategori
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = category.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Nama Kategori
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 14.sp
                )
            }

            // Label "Populer" (Hardcoded example)
            if (isPopular) {
                Surface(
                    color = Color(0xFFFF5722),
                    shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Populer",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// --- HASIL PENCARIAN ---
@Composable
fun ProductSearchResults(products: List<Product>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCardItem(product = product) // Reuse komponen dari Home
        }
    }
}

// --- HELPER: MAPPING GAMBAR KATEGORI ---
// Masukkan gambar-gambar ini ke res/drawable (ic_sayur_cat, ic_buah_cat, dll)
fun getCategoryImage(name: String): Int {
    return when {
        name.contains("Paket", true) -> R.drawable.ic_sayur // Ganti dgn gambar paket
        name.contains("Sayur", true) -> R.drawable.ic_sayur
        name.contains("Buah", true) -> R.drawable.ic_buah
        name.contains("Daging", true) -> R.drawable.ic_daging // Ganti dgn gambar daging
        name.contains("Protein", true) -> R.drawable.ic_daging
        name.contains("Bumbu", true) -> R.drawable.ic_bumbu
        name.contains("Pokok", true) -> R.drawable.ic_snack // Ganti dgn bahan pokok
        else -> R.drawable.ic_sayur
    }
}