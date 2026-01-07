package com.example.kalanacommerce.presentation.screen.dashboard.explore

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource // Import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.presentation.screen.dashboard.home.ProductCardItem
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Logika Tema
    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    val backgroundColor = if (isDarkActive) Color(0xFF121212) else Color.White
    val contentColor = if (isDarkActive) Color.White else Color.Black

    BackHandler(enabled = true) {
        when {
            uiState.searchQuery.isNotEmpty() -> viewModel.clearSearch()
            uiState.selectedCategory != null -> viewModel.clearCategory()
            else -> onBackClick()
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            ExploreHeader(
                searchQuery = uiState.searchQuery,
                onSearchChange = viewModel::onSearchQueryChange,
                isDark = isDarkActive
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {

            // --- 1. ACTIVE FILTER CHIPS ---
            if (uiState.searchQuery.isNotEmpty() || uiState.selectedCategory != null) {
                ActiveFiltersRow(
                    searchQuery = uiState.searchQuery,
                    selectedCategory = uiState.selectedCategory,
                    onClearSearch = viewModel::clearSearch,
                    onClearCategory = viewModel::clearCategory
                )

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (uiState.searchResults.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.empty_search), color = Color.Gray)
                    }
                } else {
                    ProductSearchResults(products = uiState.searchResults)
                }
            }
            // --- TAMPILAN AWAL ---
            else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // [FIX POIN 2] Memberi jarak antara SearchBar (TopBar) dengan Slider
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        AutoSlidingBanner(isDark = isDarkActive)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    item {
                        Text(
                            // [FIX POIN 3] String Resource Indonesia/Inggris
                            text = stringResource(R.string.home_category_header),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = contentColor
                        )
                    }

                    item {
                        CategoryGridSection(
                            categories = uiState.categories,
                            onCategoryClick = viewModel::onCategorySelected,
                            isDark = isDarkActive
                        )
                    }
                }
            }
        }
    }
}

// --- HEADER SEARCH ---
@Composable
fun ExploreHeader(searchQuery: String, onSearchChange: (String) -> Unit, isDark: Boolean) {
    val searchBg = if(isDark) Color(0xFF2C2C2C) else Color(0xFFF5F5F5)
    val iconColor = if(isDark) Color.Gray else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            // [FIX POIN 3] String Resource Placeholder
            placeholder = { Text(stringResource(R.string.explore_search_placeholder), fontSize = 14.sp, color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Color.Gray) },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = searchBg,
                unfocusedContainerColor = searchBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = if(isDark) Color.White else Color.Black,
                unfocusedTextColor = if(isDark) Color.White else Color.Black
            ),
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0xFF2C2C2C) else Color.White)
                .border(1.dp, if (isDark) Color.Gray.copy(0.3f) else Color(0xFFEEEEEE), CircleShape)
                .clickable { /* Go to Cart */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.ShoppingCart, null, tint = iconColor)
        }
    }
}

// --- ACTIVE FILTERS ---
@Composable
fun ActiveFiltersRow(
    searchQuery: String,
    selectedCategory: Category?,
    onClearSearch: () -> Unit,
    onClearCategory: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        if (selectedCategory != null) {
            // Mapping ulang ID ke String Resource agar Chip tampil benar
            val nameResId = when (selectedCategory.id) {
                "CAT_PAKET" -> R.string.cat_exp_packet
                "CAT_SAYUR" -> R.string.cat_exp_vegetable
                "CAT_BUAH" -> R.string.cat_exp_fruit
                "CAT_DAGING" -> R.string.cat_exp_meat
                "CAT_NABATI" -> R.string.cat_plant_protein
                "CAT_POKOK" -> R.string.cat_staple
                "CAT_BUMBU" -> R.string.cat_exp_spice
                "CAT_OLAHAN" -> R.string.cat_processed
                "CAT_INSTAN" -> R.string.cat_instant
                else -> R.string.filter_label // Fallback
            }

            FilterChipItem(
                text = "${stringResource(R.string.filter_label)} ${stringResource(nameResId)}",
                onClose = onClearCategory
            )
        }
    }
}

@Composable
fun FilterChipItem(text: String, onClose: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClose() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
        }
    }
}

// --- CATEGORY GRID (Updated UI like Reference) ---
@Composable
fun CategoryGridSection(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    isDark: Boolean
) {
    // Membagi list menjadi baris-baris berisi 3 item
    val chunkedCategories = categories.chunked(3)

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        chunkedCategories.forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Jarak antar kolom
            ) {
                rowCategories.forEach { category ->
                    Box(modifier = Modifier.weight(1f)) {
                        CategoryCard(
                            category = category,
                            onClick = { onCategoryClick(category) },
                            isDark = isDark
                        )
                    }
                }
                // Isi kekosongan jika baris terakhir kurang dari 3 item agar layout tidak melar
                if (rowCategories.size < 3) {
                    repeat(3 - rowCategories.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp)) // Jarak antar baris
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit, isDark: Boolean) {
    val context = LocalContext.current

    val (nameResId, imageRes) = when (category.id) {
        "CAT_PAKET" -> R.string.cat_exp_packet to R.drawable.ic_paketmasak
        "CAT_SAYUR" -> R.string.cat_exp_vegetable to R.drawable.ic_sayuran
        "CAT_BUAH" -> R.string.cat_exp_fruit to R.drawable.ic_buah
        "CAT_DAGING" -> R.string.cat_exp_meat to R.drawable.ic_proteinhewani
        "CAT_NABATI" -> R.string.cat_plant_protein to R.drawable.ic_proteinnabati
        "CAT_POKOK" -> R.string.cat_staple to R.drawable.ic_bahanpokok
        "CAT_BUMBU" -> R.string.cat_exp_spice to R.drawable.ic_bumbu
        "CAT_OLAHAN" -> R.string.cat_processed to R.drawable.ic_produkolahan
        "CAT_INSTAN" -> R.string.cat_instant to R.drawable.ic_bahaninstan
        else -> R.string.app_name to R.drawable.ic_logo
    }

    val displayName = stringResource(nameResId)

    // [PERBAIKAN 2] Kirim context ke helper function

    val cardContainerColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF333333)
    val isPopular = category.name.contains("Paket", ignoreCase = true) ||
            category.name.contains("Masak", ignoreCase = true) ||
            category.name.contains("Kit", ignoreCase = true) // Support Inggris

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = category.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = textColor,
                        maxLines = 2,
                        lineHeight = 16.sp
                    )
                }
            }

            if (isPopular) {
                Surface(
                    color = Color(0xFFE65100),
                    shape = RoundedCornerShape(topEnd = 12.dp, bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "Populer",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// --- PRODUCT SEARCH RESULTS ---
@Composable
fun ProductSearchResults(products: List<Product>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCardItem(product = product) // Komponen ini sudah adaptif di Home
        }
    }
}

// --- BANNER SLIDESHOW (Auto Slide & Dot Animation) ---
@Composable
fun AutoSlidingBanner(isDark: Boolean) {
    val banners = listOf(
        R.drawable.slide_1,
        R.drawable.slide_2,
        R.drawable.slide_3,
        R.drawable.slide_4
    )

    val pagerState = rememberPagerState(pageCount = { banners.size })
    // Deteksi jika user sedang menahan/drag banner agar slide tidak jalan sendiri
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(key1 = isDragged) {
        if (!isDragged) {
            while (true) {
                delay(3000) // Slide otomatis setiap 3 detik
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Image(
                    painter = painterResource(id = banners[page]),
                    contentDescription = "Promo Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Indikator Dot (Animasi panjang-pendek seperti Home Screen)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(banners.size) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val color = if (isSelected) MaterialTheme.colorScheme.primary else if(isDark) Color.White.copy(alpha = 0.3f) else Color.LightGray

                // Animasi lebar (width) dot
                val width by animateDpAsState(targetValue = if (isSelected) 32.dp else 8.dp, label = "width")

                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                        .height(6.dp)
                        .width(width)
                )
            }
        }
    }
}