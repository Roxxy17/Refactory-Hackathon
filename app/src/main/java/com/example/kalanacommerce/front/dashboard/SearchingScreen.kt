package com.example.kalanacommerce.front.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.front.theme.KalanaCommerceTheme

// Warna Putih Khusus dan Abu Terang
private val LightBackground = Color(0xFFF7F7F7)
private val WhiteSurface1 = Color.White // Putih solid

// --- OBJEK UTILITAS UNTUK DATA & KOMPONEN (TETAP SAMA) ---
object SearchUtils {
    // ... (ProductItem dan allProducts tetap sama)
    data class ProductItem(
        val id: Int,
        val name: String,
        val price: String,
        val oldPrice: String,
        val discount: String,
        val imageRes: Int,
        val tags: List<String> = emptyList()
    )

    val allProducts = listOf(
        ProductItem(1, "Selada Air", "Rp4.000", "Rp6.000", "10%", R.drawable.seladaair, listOf("sayur", "promo")),
        ProductItem(2, "Jagung", "Rp5.000", "Rp7.000", "15%", R.drawable.jagung, listOf("sayur", "promo")),
        ProductItem(3, "Kentang", "Rp6.500", "Rp8.000", "20%", R.drawable.kentang, listOf("umbi", "promo")),
        ProductItem(4, "Ayam", "Rp20.000", "Rp25.000", "20%", R.drawable.ayam, listOf("daging", "sop")),
        ProductItem(5, "Kol", "Rp3.000", "Rp4.000", "10%", R.drawable.kol, listOf("sayur", "sop")),
        ProductItem(6, "Wortel", "Rp4.000", "Rp5.000", "10%", R.drawable.wortel, listOf("sayur", "sop")),
        ProductItem(7, "Seledri", "Rp2.000", "Rp3.000", "10%", R.drawable.seledri, listOf("sayur", "sop"))
    )

    fun filterProducts(products: List<ProductItem>, query: String): List<ProductItem> {
        if (query.isBlank()) return products
        val lowerQuery = query.trim().lowercase()
        return products.filter { item ->
            item.name.lowercase().contains(lowerQuery) ||
                    item.tags.any { it.contains(lowerQuery) }
        }
    }

    @Composable
    fun ProductCard(item: ProductItem) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryColor = MaterialTheme.colorScheme.secondary
        val darkText = MaterialTheme.colorScheme.onSurface

        var count by remember(item.id) { mutableStateOf(0) }
        val isPromo = item.tags.contains("promo")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteSurface1), // PUTIH
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.width(160.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {

                Box(
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightBackground), // Background gambar tetap abu terang
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(0.9f),
                        contentScale = ContentScale.Fit
                    )
                    if (isPromo) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(secondaryColor, shape = RoundedCornerShape(bottomStart = 8.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = item.discount,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp)
                ) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = darkText,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            if (isPromo) {
                                Text(
                                    text = item.oldPrice,
                                    color = Color.Gray.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    style = LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough)
                                )
                            }
                            Text(
                                text = item.price,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                        }

                        if (count == 0) {
                            FloatingActionButton(
                                onClick = { count++ },
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                containerColor = primaryColor,
                                contentColor = Color.White,
                                elevation = FloatingActionButtonDefaults.elevation(0.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WhiteSurface1.copy(alpha = 0.9f)) // PUTIH
                                    .border(1.dp, primaryColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Remove",
                                    tint = primaryColor,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { count-- }
                                        .padding(4.dp)
                                )
                                Text(
                                    text = count.toString(),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = darkText
                                )
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(primaryColor)
                                        .clickable { count++ }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
// --- AKHIR OBJEK UTILITAS ---


// --- Composable Utama SearchingScreen ---

@Composable
fun SearchingScreen(onBack: () -> Unit = {}) {
    // Mengakses skema warna dari tema
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val darkText = colorScheme.onSurface

    var searchQuery by remember { mutableStateOf("") }
    var isFilterActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onBack = onBack,
                onFilterClick = { isFilterActive = !isFilterActive },
                isFilterApplied = isFilterActive,
                primaryColor = primaryColor,
                searchContainerColor = LightBackground, // Search field menggunakan abu terang (LightBackground)
                darkText = darkText
            )
        },
        containerColor = WhiteSurface1 // Latbar Scaffold PUTIH SOLID
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            val filteredProducts = SearchUtils.filterProducts(SearchUtils.allProducts, searchQuery)

            // Indikator jumlah hasil yang lebih menonjol
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhiteSurface1) // Pastikan baris ini putih
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredProducts.size} Hasil",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = darkText,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (searchQuery.isNotBlank()) "untuk \"$searchQuery\"" else "Semua Produk",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Divider tipis
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // ... (Konten kosong)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WhiteSurface1), // Grid area PUTIH
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { item ->
                        SearchUtils.ProductCard(item = item)
                    }
                }
            }
        }
    }
}

// --- Komponen TopBar Pencarian yang Ditingkatkan (Diperbarui untuk Putih) ---

@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onFilterClick: () -> Unit,
    isFilterApplied: Boolean,
    primaryColor: Color,
    searchContainerColor: Color, // Warna abu terang (F7F7F7)
    darkText: Color
) {
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = WhiteSurface, // Latar belakang TopBar PUTIH
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = darkText)
            }

            // Search Field
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Cari produk...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = searchContainerColor, // Abu terang
                    unfocusedContainerColor = searchContainerColor, // Abu terang
                    cursorColor = primaryColor,
                    focusedTextColor = darkText,
                    unfocusedTextColor = darkText
                )
            )

            // Tombol Filter dengan indikator
            Box {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = if (isFilterApplied) primaryColor else Color.Gray
                    )
                }
                if (isFilterApplied) {
                    // Indikator kecil jika filter diterapkan
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = (4).dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(secondaryColor)
                    )
                }
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun SearchingScreenPreview() {
    KalanaCommerceTheme {
        SearchingScreen()
    }
}