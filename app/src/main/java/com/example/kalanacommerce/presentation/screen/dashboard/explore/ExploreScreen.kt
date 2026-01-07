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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.TextStyle
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

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    val backgroundImage = if (isDarkActive) {
        R.drawable.splash_background_black
    } else {
        R.drawable.splash_background_white
    }

    val contentColor = if (isDarkActive) Color.White else Color.Black

    BackHandler(enabled = true) {
        when {
            uiState.searchQuery.isNotEmpty() -> viewModel.clearSearch()
            uiState.selectedCategory != null -> viewModel.clearCategory()
            else -> onBackClick()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // LAYER 1: Background Image (Paling Bawah)
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // LAYER 2: Konten Utama (Scrollable)
        // Kita tidak pakai Scaffold, tapi langsung Column/LazyGrid
        Column(modifier = Modifier.fillMaxSize()) {

            // Logika Konten (Grid / Search Results)
            if (uiState.searchQuery.isNotEmpty() || uiState.selectedCategory != null) {
                // ... (Kode Active Filters & Search Result tetap sama, tapi tambahkan spacer di atas) ...
                // Spacer agar tidak ketabrak Header yang mengambang nanti
                Spacer(modifier = Modifier.height(100.dp))

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
                        Text(stringResource(R.string.empty_search), color = contentColor)
                    }
                } else {
                    ProductSearchResults(products = uiState.searchResults)
                }
            } else {
                // Tampilan Awal (Grid Kategori)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    // [PENTING] Tambahkan top padding yang cukup besar agar konten awal
                    // muncul DI BAWAH Search Bar, bukan di belakangnya.
                    // bottom padding agar tidak tertutup nav bar.
                    contentPadding = PaddingValues(top = 110.dp, bottom = 100.dp)
                ) {
                    item { AutoSlidingBanner(isDark = isDarkActive) }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    item {
                        Text(
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

        ExploreHeader(
            searchQuery = uiState.searchQuery,
            onSearchChange = viewModel::onSearchQueryChange,
            isDark = isDarkActive,
            modifier = Modifier.align(Alignment.TopCenter) // Pastikan nempel di atas
        )
    }
}

@Composable
fun thickGlossyModifier(isDark: Boolean, shape: androidx.compose.ui.graphics.Shape): Modifier {
    val glassColor = if (isDark) Color.Black.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.90f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.5f)
    return Modifier
        .shadow(elevation = 6.dp, shape = shape, spotColor = Color.Black.copy(alpha = 0.1f))
        .background(glassColor, shape)
        .border(1.dp, borderColor, shape)
        .clip(shape)
}

// --- [UPDATE] Explore Header (Search Bar + Cart) menjadi Glossy ---
@Composable
fun ExploreHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier // Terima modifier agar bisa diatur posisinya
) {
    val iconColor = if(isDark) Color.Gray else Color.Black
    val placeholderColor = Color.Gray
    val textColor = if(isDark) Color.White else Color.Black

    Row(
        modifier = modifier // Gunakan modifier dari parameter
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 4.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ... (Isi Search Bar & Cart SAMA PERSIS dengan kode sebelumnya) ...
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = textColor),
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .then(thickGlossyModifier(isDark, RoundedCornerShape(30.dp))),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Outlined.Search, null, tint = iconColor, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (searchQuery.isEmpty()) {
                            Text(stringResource(R.string.explore_search_placeholder), fontSize = 14.sp, color = placeholderColor, maxLines = 1)
                        }
                        innerTextField()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(50.dp)
                .then(thickGlossyModifier(isDark, CircleShape))
                .clickable { /* Go to Cart */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.ShoppingCart, null, tint = iconColor)
        }
    }
}

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
        if (searchQuery.isNotEmpty()) {
            FilterChipItem(
                text = "${stringResource(R.string.search_label)} \"$searchQuery\"",
                onClose = onClearSearch
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        if (selectedCategory != null) {
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
                else -> R.string.filter_label
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

@Composable
fun CategoryGridSection(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    isDark: Boolean
) {
    val chunkedCategories = categories.chunked(3)
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        chunkedCategories.forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                if (rowCategories.size < 3) {
                    repeat(3 - rowCategories.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit, isDark: Boolean) {
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
    val cardContainerColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF333333)
    val isPopular = category.id == "CAT_PAKET"

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
                    contentDescription = displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
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

@Composable
fun ProductSearchResults(products: List<Product>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCardItem(product = product)
        }
    }
}

@Composable
fun AutoSlidingBanner(isDark: Boolean) {
    val banners = listOf(R.drawable.slide_1, R.drawable.slide_2, R.drawable.slide_3, R.drawable.slide_4)
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(key1 = isDragged) {
        if (!isDragged) {
            while (true) {
                delay(3000)
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
                modifier = Modifier.fillMaxWidth().height(150.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Image(painter = painterResource(id = banners[page]), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(banners.size) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val color = if (isSelected) MaterialTheme.colorScheme.primary else if(isDark) Color.White.copy(alpha = 0.3f) else Color.LightGray
                val width by animateDpAsState(targetValue = if (isSelected) 32.dp else 8.dp, label = "width")
                Box(modifier = Modifier.padding(horizontal = 2.dp).clip(RoundedCornerShape(4.dp)).background(color).height(6.dp).width(width))
            }
        }
    }
}