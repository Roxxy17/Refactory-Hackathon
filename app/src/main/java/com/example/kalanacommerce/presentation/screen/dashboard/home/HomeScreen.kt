package com.example.kalanacommerce.presentation.screen.dashboard.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.presentation.components.CustomToast
import com.example.kalanacommerce.presentation.components.PullToRefreshWrapper
import com.example.kalanacommerce.presentation.components.ToastType
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.material.icons.outlined.Inventory2 // Untuk Paket
import androidx.compose.material.icons.outlined.Spa        // Untuk Nabati
import androidx.compose.material.icons.outlined.ShoppingBag // Untuk Bahan Pokok
import androidx.compose.material.icons.outlined.DinnerDining // Untuk Olahan
import androidx.compose.material.icons.outlined.RamenDining // Untuk Instan
import androidx.compose.material.icons.outlined.Category // Fallback
import com.example.kalanacommerce.presentation.components.ProductCardItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onProductClick: (String) -> Unit,
    onNavigateToCheckout: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- STATE UNTUK TOAST ---
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.Success) }

    // --- BACKGROUND LOGIC ---
    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    LaunchedEffect(uiState.navigateToCheckoutWithId) {
        uiState.navigateToCheckoutWithId?.let { destination ->
            onNavigateToCheckout(destination) // Panggil navigasi
            viewModel.clearMessages() // Reset state agar tidak terpanggil 2x
        }
    }

    // --- LISTENER PESAN DARI VIEWMODEL ---
    LaunchedEffect(uiState.error, uiState.successMessage) {
        if (uiState.error != null) {
            toastMessage = uiState.error!!
            toastType = ToastType.Error
            showToast = true
            viewModel.clearMessages()
        }
        if (uiState.successMessage != null) {
            toastMessage = uiState.successMessage!!
            toastType = ToastType.Success
            showToast = true
            viewModel.clearMessages()
        }
    }

    val bgImage =
        if (isDarkActive) R.drawable.background_home_black else R.drawable.background_home_white

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0.dp)
        ) { paddingValues ->
            PullToRefreshWrapper(
                isRefreshing = uiState.isRefreshing,
                onRefresh = {
                    // [UBAH DARI refreshData() KE loadHomeData(true)]
                    viewModel.loadHomeData(isPullRefresh = true)
                },
                modifier = Modifier.padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {

                    // --- SECTION 1: HEADER TEXT ---
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.home_hero_title),
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    lineHeight = 42.sp,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                        }
                    }

                    // --- SECTION 2: SEARCH BAR (STICKY) ---
                    stickyHeader {
                        StickySearchBar(
                            searchQuery = uiState.searchQuery,
                            onSearchChange = viewModel::onSearchQueryChange,
                            isDark = isDarkActive
                        )
                    }

                    // --- SECTION 3: HEADER "CEK UPDATE" & BANNER ---
                    item {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            PaddingWrapper {
                                Text(
                                    text = stringResource(R.string.home_latest_products),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            BannerCarousel(isDark = isDarkActive)
                        }
                    }

                    // --- SECTION 4: CATEGORY ROW (STICKY & STATIC) ---
                    stickyHeader {
                        Spacer(modifier = Modifier.height(16.dp))
                        // [MODIFIKASI] Kita kirim categories ASLI dari backend hanya untuk pencocokan ID
                        StickyCategoryRow(
                            availableCategories = uiState.categories,
                            selectedCategoryId = uiState.selectedCategoryId,
                            onCategorySelect = viewModel::onCategorySelected,
                            isDark = isDarkActive
                        )
                    }

                    // --- SECTION 5: HEADER "PROMO BULAN INI" ---
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        PaddingWrapper {
                            Text(
                                text = stringResource(R.string.home_promo_title),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // --- SECTION 6: PRODUCT GRID ---
                    if (uiState.isLoading && !uiState.isRefreshing) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    } else if (uiState.displayProducts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.home_empty_product),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        val chunkedProducts = uiState.displayProducts.chunked(2)
                        items(chunkedProducts) { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                for (product in rowItems) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        ProductCardItem(
                                            product = product,
                                            onClick = onProductClick,
                                            onQuickAddToCart = { selectedProduct, qty ->
                                                viewModel.onAddToCart(selectedProduct, qty)
                                            },
                                            onQuickBuyNow = { selectedProduct, qty ->
                                                viewModel.onBuyNow(selectedProduct, qty)
                                            }
                                        )
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        CustomToast(
            message = toastMessage,
            isVisible = showToast,
            type = toastType,
            onDismiss = { showToast = false }
        )
    }
}


// --- HELPER WRAPPER ---
@Composable
fun PaddingWrapper(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 24.dp)) { content() }
}

// --- HELPER: THICK GLOSSY EFFECT ---
@Composable
fun thickGlossyModifier(isDark: Boolean, shape: androidx.compose.ui.graphics.Shape): Modifier {
    val glassColor =
        if (isDark) Color.Black.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.90f)
    val borderColor =
        if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.5f)
    return Modifier
        .shadow(elevation = 6.dp, shape = shape, spotColor = Color.Black.copy(alpha = 0.1f))
        .background(glassColor, shape)
        .border(1.dp, borderColor, shape)
        .clip(shape)
}

// --- STICKY 1: SEARCH BAR ---
@Composable
fun StickySearchBar(searchQuery: String, onSearchChange: (String) -> Unit, isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        stringResource(R.string.home_search_placeholder),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Search,
                        "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                Icons.Default.Close,
                                "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .then(thickGlossyModifier(isDark, RoundedCornerShape(30.dp))),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .then(thickGlossyModifier(isDark, CircleShape))
                    .clickable { /* Cart */ }
            ) {
                Icon(
                    Icons.Outlined.ShoppingCart,
                    "Cart",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// --- STICKY 2: CATEGORY ROW (STATIC) ---
@Composable
fun StickyCategoryRow(
    availableCategories: List<Category>,
    selectedCategoryId: String,
    onCategorySelect: (String) -> Unit,
    isDark: Boolean
) {
    // [MODIFIKASI] Daftar kategori LENGKAP (Sama dengan Explore)
    val staticTypes = listOf(
        "Semua",
        "Paket Masak",
        "Sayur Segar",
        "Buah Segar",
        "Protein Hewani",
        "Protein Nabati",
        "Bahan Pokok",
        "Bumbu",
        "Produk Olahan",
        "Bahan Instan",
        "Snack"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .then(thickGlossyModifier(isDark, RoundedCornerShape(50)))
        ) {

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                items(staticTypes) { typeName ->

                    // Logic pencocokan ID Backend (agar filter berfungsi)
                    val keyword = when (typeName) {
                        "Paket Masak" -> "Paket"
                        "Sayur Segar" -> "Sayur"
                        "Buah Segar" -> "Buah"
                        "Protein Hewani" -> "Daging"
                        "Protein Nabati" -> "Nabati"
                        "Bahan Pokok" -> "Sembako"
                        "Bumbu" -> "Bumbu Dapur"
                        "Produk Olahan" -> "Olahan"
                        "Bahan Instan" -> "Instan"
                        else -> typeName
                    }

                    val matchedCategory =
                        if (typeName == "Semua") null else availableCategories.find {
                            it.name.contains(keyword, ignoreCase = true)
                        }
                    val realId = if (typeName == "Semua") "ALL" else matchedCategory?.id

                    // Cek Seleksi
                    val isSelected = if (typeName == "Semua") {
                        selectedCategoryId == "ALL"
                    } else {
                        realId == selectedCategoryId || (realId == null && selectedCategoryId == "DUMMY_${typeName}")
                    }

                    val visualCategory = Category(id = realId ?: "STATIC", name = typeName)

                    WhatsAppStylePill(
                        category = visualCategory,
                        isSelected = isSelected,
                        onSelect = {
                            if (realId != null) onCategorySelect(realId)
                            else onCategorySelect("DUMMY_${typeName}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WhatsAppStylePill(
    category: Category,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val containerColor by animateColorAsState(
        if (isSelected) primaryColor else Color.Transparent,
        label = "pillBg"
    )
    val contentColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.onPrimary else primaryColor,
        label = "pillContent"
    )

    Surface(
        onClick = onSelect,
        color = containerColor,
        shape = RoundedCornerShape(50),
        modifier = Modifier.height(44.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // Icon Statis dari PNG
            val iconData = getCategoryIcon(category.name)

            when (iconData) {
                is CategoryIcon.Vector -> {
                    Icon(
                        imageVector = iconData.imageVector,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                is CategoryIcon.Drawable -> {
                    Icon(
                        painter = painterResource(id = iconData.resId),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    // Translate Nama
                    val nameResId = getCategoryNameResId(category.name)
                    Text(
                        text = if (nameResId != 0) stringResource(nameResId) else category.name,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = contentColor
                    )
                }
            }
        }
    }
}

// --- BANNER CAROUSEL ---
// --- BANNER CAROUSEL (UPDATED LOGIC) ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCarousel(isDark: Boolean) {
    val pagerState = rememberPagerState(pageCount = { 4 })

    // [LOGIKA BARU DARI EXPLORE] Deteksi interaksi drag
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(key1 = isDragged) {
        if (!isDragged) {
            while (true) {
                delay(3000) // Geser setiap 3 detik
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp
        ) { page ->
            val imageRes = when (page) {
                0 -> R.drawable.slide_1
                1 -> R.drawable.slide_2 // Diganti slide_2 biar variatif
                2 -> R.drawable.slide_3 // Diganti slide_3
                else -> R.drawable.slide_4
            }
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .border(
                        1.dp,
                        if (isDark) Color.White.copy(alpha = 0.2f) else Color.White,
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Indikator Dot (Tetap menggunakan gaya Home yg diminta)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Spacer(modifier = Modifier.width(24.dp))
            repeat(pagerState.pageCount) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val color =
                    if (isSelected) MaterialTheme.colorScheme.primary else if (isDark) Color.White.copy(
                        alpha = 0.3f
                    ) else Color.Black.copy(alpha = 0.2f)
                // Animasi lebar dot
                val width by animateDpAsState(
                    targetValue = if (isSelected) 32.dp else 12.dp,
                    label = "width"
                )

                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                        .height(6.dp)
                        .width(width)
                )
            }
        }
    }
}

// --- HELPER FUNCTIONS FOR MAPPING & ICONS ---
fun getCategoryIcon(name: String): CategoryIcon {
    return when {
        // 1. Kategori yang Anda Minta Pakai GAMBAR (Drawable)
        name.contains(
            "Semua",
            true
        ) -> CategoryIcon.Drawable(R.drawable.ic_sayur) // Pakai ic_sayur untuk 'Semua'
        name.contains("Buah", true) -> CategoryIcon.Drawable(R.drawable.ic_buah2)
        name.contains("Daging", true) || name.contains(
            "Hewani",
            true
        ) -> CategoryIcon.Drawable(R.drawable.ic_daging)

        name.contains("Bumbu", true) -> CategoryIcon.Drawable(R.drawable.ic_bumbu2)
        name.contains("Snack", true) -> CategoryIcon.Drawable(R.drawable.ic_snack)

        // 2. Kategori Sisanya Pakai VECTOR ICON (Bawaan Android)
        name.contains("Sayur", true) -> CategoryIcon.Vector(Icons.Outlined.Eco) // Daun
        name.contains(
            "Paket",
            true
        ) -> CategoryIcon.Vector(Icons.Outlined.Inventory2) // Kotak Kardus
        name.contains("Nabati", true) -> CategoryIcon.Vector(Icons.Outlined.Spa) // Tunas/Kedelai
        name.contains("Pokok", true) || name.contains(
            "Sembako",
            true
        ) -> CategoryIcon.Vector(Icons.Outlined.ShoppingBag) // Tas Belanja
        name.contains(
            "Olahan",
            true
        ) -> CategoryIcon.Vector(Icons.Outlined.DinnerDining) // Makanan Jadi
        name.contains(
            "Instan",
            true
        ) -> CategoryIcon.Vector(Icons.Outlined.RamenDining) // Mangkok Mie

        // Fallback
        else -> CategoryIcon.Vector(Icons.Outlined.Category)
    }
}

fun getCategoryNameResId(apiName: String): Int {
    return when {
        apiName.contains("Semua", true) -> R.string.cat_other // Atau buat string "Semua"
        apiName.contains("Paket", true) -> R.string.cat_exp_packet
        apiName.contains("Sayur", true) -> R.string.cat_exp_vegetable
        apiName.contains("Buah", true) -> R.string.cat_exp_fruit
        apiName.contains("Daging", true) || apiName.contains(
            "Hewani",
            true
        ) -> R.string.cat_exp_meat

        apiName.contains("Nabati", true) -> R.string.cat_plant_protein
        apiName.contains("Pokok", true) || apiName.contains("Sembako", true) -> R.string.cat_staple
        apiName.contains("Bumbu", true) -> R.string.cat_exp_spice
        apiName.contains("Olahan", true) -> R.string.cat_processed
        apiName.contains("Instan", true) -> R.string.cat_instant
        apiName.contains("Snack", true) -> R.string.cat_snack
        else -> 0
    }
}

// Helper class untuk menampung jenis icon (bisa Vector atau Drawable)
sealed class CategoryIcon {
    data class Vector(val imageVector: androidx.compose.ui.graphics.vector.ImageVector) :
        CategoryIcon()

    data class Drawable(val resId: Int) : CategoryIcon()
}

// Unit helper sudah dihapus karena kita pakai product.variantName