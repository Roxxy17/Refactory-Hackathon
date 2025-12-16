package com.example.kalanacommerce.front.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.front.theme.KalanaCommerceTheme

// --- Palet Warna Ditingkatkan ---
val PrimaryGreen = Color(0xFF069C6F) // Hijau Utama yang Lebih Segar
val SecondaryOrange = Color(0xFFFF6B00) // Oranye Aksen
val BackgroundLight = Color(0xFFF7F7F7) // Latar belakang yang lebih bersih
val DarkText = Color(0xFF1A1A1A) // Teks yang lebih gelap
val LightText = Color(0xFF888888) // Teks abu-abu
val WhiteSurface = Color.White

// --- Data Model Kategori (Tetap Sama) ---
data class CategoryItem(val name: String, val icon: ImageVector, val color: Color)

val dummyCategories = listOf(
    CategoryItem("Sayur", Icons.Filled.LocalFlorist, PrimaryGreen),
    CategoryItem("Daging", Icons.Filled.Fastfood, PrimaryGreen),
    CategoryItem("Bumbu", Icons.Filled.SetMeal, PrimaryGreen),
    CategoryItem("Buah", Icons.Filled.Coronavirus, PrimaryGreen),
    CategoryItem("Snack", Icons.Filled.BakeryDining, PrimaryGreen),
    CategoryItem("Ikan", Icons.Filled.SetMeal, PrimaryGreen),
)

// --- Data Classes Produk (Tetap Sama) ---
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
    ProductItem(1, "Selada Air Segar", "Rp4.000", "Rp6.000", "33%", R.drawable.seladaair, listOf("sayur", "promo")),
    ProductItem(2, "Jagung Manis", "Rp5.000", "Rp7.000", "28%", R.drawable.jagung, listOf("sayur", "promo")),
    ProductItem(3, "Kentang Dieng", "Rp6.500", "Rp8.000", "18%", R.drawable.kentang, listOf("umbi", "promo")),
    ProductItem(4, "Daging Ayam Fillet", "Rp20.000", "Rp25.000", "20%", R.drawable.ayam, listOf("daging", "sop")),
    ProductItem(5, "Kol Putih", "Rp3.000", "Rp4.000", "25%", R.drawable.kol, listOf("sayur", "sop")),
    ProductItem(6, "Wortel Import", "Rp4.000", "Rp5.000", "20%", R.drawable.wortel, listOf("sayur", "sop")),
    ProductItem(7, "Seledri Lokal", "Rp2.000", "Rp3.000", "33%", R.drawable.seledri, listOf("sayur", "sop"))
)

// =====================================================================
// === MAIN SCREEN ===
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Menggunakan TopAppBarState untuk efek Scroll
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Header menggunakan TopAppBar Material 3 standar untuk integrasi scroll
            HomeTopAppBar(scrollBehavior)
        },
        containerColor = BackgroundLight
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- UI KIT AREA: Search Bar dan Kategori (Tetap di atas, tidak ikut scroll) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhiteSurface)
                    .padding(vertical = 16.dp),
            ) {
                // Search Bar
                CustomSearchBar(searchQuery) { searchQuery = it }
                Spacer(modifier = Modifier.height(16.dp))
                // Kategori Populer Horizontal
                CategoryRow(dummyCategories)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Slide Banner ---
            SlideBanner(Modifier.padding(horizontal = 16.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // --- Konten Dinamis yang di Filter ---
            val filteredProducts = filterProducts(allProducts, searchQuery)
            val promoProducts = filteredProducts.filter { it.tags.contains("promo") }
            val sopProducts = filteredProducts.filter { it.tags.contains("sop") }

            if (searchQuery.isBlank() || promoProducts.isNotEmpty()) {
                SectionTitle("Promo Spesial", modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(12.dp))
                ProductRow(products = promoProducts)
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (searchQuery.isBlank() || sopProducts.isNotEmpty()) {
                SectionTitle("Paket Masakan Harian", modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(12.dp))
                ProductRow(products = sopProducts)
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (promoProducts.isEmpty() && sopProducts.isEmpty() && searchQuery.isNotBlank()) {
                Text(
                    text = "Tidak ada hasil untuk \"$searchQuery\"",
                    color = LightText,
                    modifier = Modifier.padding(32.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

// =====================================================================
// === NEW & IMPROVED COMPONENTS ===
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "KALANA",
                color = WhiteSurface,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* Handle Menu Click */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = WhiteSurface)
            }
        },
        actions = {
            IconButton(onClick = { /* Handle Notifications Click */ }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist", tint = WhiteSurface)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PrimaryGreen,
            scrolledContainerColor = PrimaryGreen // Tetap hijau saat di-scroll
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun CustomSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari produk atau resep...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = LightText) },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = LightText)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp), // Radius sedikit dikurangi
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.Transparent, // Menghilangkan border agar terlihat clean
            focusedContainerColor = WhiteSurface,
            unfocusedContainerColor = WhiteSurface,
            cursorColor = PrimaryGreen
        )
    )
}

@Composable
fun CategoryRow(categories: List<CategoryItem>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { item ->
            CategoryItemCard(item)
        }
    }
}

@Composable
fun CategoryItemCard(item: CategoryItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(60.dp)
            .clickable(onClick = { /* Handle category click */ })
    ) {
        Card(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp), // Bentuk sedikit lebih besar
            colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.1f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, item.color.copy(alpha = 0.2f), RoundedCornerShape(14.dp)), // Menambahkan border halus
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = item.name, tint = item.color, modifier = Modifier.size(26.dp))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(item.name, fontSize = 12.sp, color = DarkText, maxLines = 1, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SlideBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp), // Ketinggian sedikit ditambah
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Bayangan lebih nyata
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 20.dp, end = 8.dp)
            ) {
                Text(
                    text = "Diskon Hingga 50%",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Bahan Masakan Segar!",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    lineHeight = 28.sp
                )
            }
            // Tambahkan elemen visual dummy (seperti ikon keranjang besar)
            Icon(
                Icons.Default.ShoppingBasket,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(90.dp)
                    .weight(0.4f)
                    .padding(end = 16.dp)
            )
        }
    }
}

// =====================================================================
// === PRODUCT CARD (Ditingkatkan) ===
// =====================================================================

@Composable
fun ProductCard(item: ProductItem) {
    var count by remember(item.id) { mutableStateOf(0) }
    val isPromo = item.tags.contains("promo")

    Card(
        shape = RoundedCornerShape(12.dp), // Radius sedikit dikurangi
        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp), // Bayangan lebih halus
        modifier = Modifier
            .width(160.dp)
            .clickable { /* Handle product click */ }
    ) {
        Column {

            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(BackgroundLight),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(0.9f),
                    contentScale = ContentScale.Fit
                )
                if (isPromo) {
                    // Badge diskon yang lebih menonjol
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = SecondaryOrange)
                    ) {
                        Text(
                            text = "-${item.discount}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Detail Produk
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis // Menambahkan ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Harga dan Aksi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (isPromo) {
                            Text(
                                text = item.oldPrice,
                                color = LightText,
                                fontSize = 11.sp,
                                style = LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough)
                            )
                        }
                        Text(
                            text = item.price,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = PrimaryGreen
                        )
                    }

                    // Tombol Tambah/Counter
                    ProductQuantityControl(count) { newCount -> count = newCount }
                }
            }
        }
    }
}

@Composable
fun ProductQuantityControl(count: Int, onCountChange: (Int) -> Unit) {
    if (count == 0) {
        FloatingActionButton(
            onClick = { onCountChange(count + 1) },
            modifier = Modifier.size(36.dp), // Ukuran sedikit lebih besar
            shape = CircleShape,
            containerColor = PrimaryGreen,
            contentColor = WhiteSurface,
            elevation = FloatingActionButtonDefaults.elevation(2.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(20.dp))
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, PrimaryGreen, RoundedCornerShape(8.dp)) // Border hijau
        ) {
            IconButton(
                onClick = { onCountChange(count - 1) },
                modifier = Modifier.size(36.dp),
                enabled = count > 0,
                colors = IconButtonDefaults.iconButtonColors(contentColor = PrimaryGreen, containerColor = WhiteSurface)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Remove", modifier = Modifier.size(18.dp))
            }
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp),
                color = PrimaryGreen
            )
            Text(
                text = count.toString(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = DarkText,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp),
                color = PrimaryGreen
            )
            IconButton(
                onClick = { onCountChange(count + 1) },
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = WhiteSurface, containerColor = PrimaryGreen)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
            }
        }
    }
}


// --- Sisanya Tetap Sama ---

fun filterProducts(products: List<ProductItem>, query: String): List<ProductItem> {
    if (query.isBlank()) return products
    val lowerQuery = query.trim().lowercase()
    return products.filter { item ->
        item.name.lowercase().contains(lowerQuery) ||
                item.tags.any { it.contains(lowerQuery) }
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp // Ukuran lebih besar
            ),
            color = DarkText,
        )
        Text(
            text = "Lihat Semua",
            color = PrimaryGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { /* Navigasi ke daftar produk */ }
        )
    }
}

@Composable
fun ProductRow(products: List<ProductItem>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { item ->
            ProductCard(item)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreScreenPreview() {
    KalanaCommerceTheme {
        ExploreScreen()
    }
}