package com.example.kalanacommerce.presentation.screen.dashboard.home

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.presentation.theme.KalanaCommerceTheme

// --- Data Model (Tetap Sama) ---
data class CategoryItem(val name: String, val icon: ImageVector)

val dummyCategories = listOf(
    CategoryItem("Sayur", Icons.Filled.LocalFlorist),
    CategoryItem("Daging", Icons.Filled.Fastfood),
    CategoryItem("Bumbu", Icons.Filled.SetMeal),
    CategoryItem("Buah", Icons.Filled.Coronavirus),
    CategoryItem("Snack", Icons.Filled.BakeryDining),
    CategoryItem("Ikan", Icons.Filled.SetMeal),
)

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
fun HomeScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }

    // Scroll Behavior untuk TopBar yang dinamis (Menghilang/Mengecil saat scroll)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(scrollBehavior)
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp)// Latar belakang adaptif tema
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- SEARCH BAR AREA ---
            // Diletakkan di sini agar ikut scroll, memberikan kesan "menyatu"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        // Latar belakang header mengikuti warna container TopBar saat expanded
                        MaterialTheme.colorScheme.surface
                    )
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 8.dp)
            ) {
                CustomSearchBar(searchQuery) { searchQuery = it }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- KATEGORI ---
            CategoryRow(dummyCategories)

            Spacer(modifier = Modifier.height(20.dp))

            // --- SLIDE BANNER ---
            SlideBanner(Modifier.padding(horizontal = 16.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // --- LOGIKA FILTER PRODUK ---
            val filteredProducts = filterProducts(allProducts, searchQuery)
            val promoProducts = filteredProducts.filter { it.tags.contains("promo") }
            val sopProducts = filteredProducts.filter { it.tags.contains("sop") }

            // --- SECTION 1: PROMO SPESIAL ---
            if (searchQuery.isBlank() || promoProducts.isNotEmpty()) {
                SectionTitle("Promo Spesial 🔥", modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(12.dp))
                ProductRow(products = promoProducts)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- SECTION 2: PAKET MASAKAN ---
            if (searchQuery.isBlank() || sopProducts.isNotEmpty()) {
                SectionTitle("Bahan Sop Segar 🍲", modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(12.dp))
                ProductRow(products = sopProducts)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- EMPTY STATE ---
            if (promoProducts.isEmpty() && sopProducts.isEmpty() && searchQuery.isNotBlank()) {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Tidak ada hasil untuk \"$searchQuery\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Bottom Padding untuk Floating Action Button atau BottomNav
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// =====================================================================
// === COMPONENTS (THEME AWARE) ===
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    LargeTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Bisa ganti Text dengan Logo Image jika ada
                Text(
                    text = "Kalana",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp
                    )
                )
                Text(
                    text = ".",
                    color = MaterialTheme.colorScheme.secondary, // Aksen titik warna oranye/secondary
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Notifikasi */ }) {
                // Badge notifikasi (opsional)
                Box {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifikasi",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            IconButton(onClick = { /* Wishlist */ }) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface, // Tetap solid saat scroll
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun CustomSearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                "Cari sayur, buah, bumbu...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        },
        leadingIcon = {
            Icon(
                Icons.Outlined.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(26.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(26.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest, // Sedikit abu/terang
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = true
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
            .width(64.dp)
            .clickable(onClick = { /* Handle category click */ })
    ) {
        // Container Ikon
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            // Menggunakan warna container sekunder dari tema (biasanya soft color)
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.name,
                    // Tint icon mengikuti onSecondaryContainer agar kontras
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SlideBanner(modifier: Modifier = Modifier) {
    // Warna gradient dinamis
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
        ) {
            // Dekorasi lingkaran transparan
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.1f),
                    center = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    radius = size.height
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Promo Mingguan",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sayur Segar\nLangsung Petani!",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Diskon s.d 50%",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Gambar/Icon Ilustrasi Banner
                Icon(
                    imageVector = Icons.Default.ShoppingBasket,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(start = 16.dp)
                )
            }
        }
    }
}

// =====================================================================
// === PRODUCT CARD (Modern & Themed) ===
// =====================================================================

@Composable
fun ProductCard(item: ProductItem) {
    var count by remember(item.id) { mutableIntStateOf(0) }
    val isPromo = item.tags.contains("promo")

    // Card menggunakan surface color dari tema
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow // Warna kartu sedikit beda dari background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(170.dp)
            .clickable { /* Handle product click */ }
    ) {
        Column {
            // Image Area
            Box(
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer) // Placeholder color
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Diskon Badge
                if (isPromo) {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd),
                        color = MaterialTheme.colorScheme.error, // Warna merah/error untuk diskon
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "-${item.discount}",
                            color = MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Product Details
            Column(modifier = Modifier.padding(12.dp)) {
                // Nama Produk
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(40.dp) // Fixed height untuk alignment
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Harga
                Column {
                    if (isPromo) {
                        Text(
                            text = item.oldPrice,
                            style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = item.price,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tombol Tambah / Counter
                ProductQuantityControl(count) { newCount -> count = newCount }
            }
        }
    }
}

@Composable
fun ProductQuantityControl(count: Int, onCountChange: (Int) -> Unit) {
    if (count == 0) {
        // Tombol "+ Keranjang" Penuh
        Button(
            onClick = { onCountChange(count + 1) },
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "+ Keranjang",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    } else {
        // Counter Control
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) {
            // Minus Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onCountChange(count - 1) }
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }

            // Count Text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Plus Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onCountChange(count + 1) }
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// --- Helper Functions ---
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = { /* Navigasi lihat semua */ }) {
            Text(
                text = "Lihat Semua",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }
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
fun HomeScreenPreviewLight() {
    KalanaCommerceTheme(darkTheme = false) {
        HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewDark() {
    KalanaCommerceTheme(darkTheme = true) {
        HomeScreen()
    }
}