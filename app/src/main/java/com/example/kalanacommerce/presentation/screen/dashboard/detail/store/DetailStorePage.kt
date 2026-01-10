package com.example.kalanacommerce.presentation.screen.dashboard.detail.store

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.presentation.components.ProductCardItem
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@Composable
fun DetailStorePage(
    outletId: String,
    viewModel: DetailStoreViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit
) {
    LaunchedEffect(outletId) {
        viewModel.loadStoreData(outletId)
    }

    val uiState by viewModel.uiState.collectAsState()

    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    val backgroundImage =
        if (isDarkActive) R.drawable.splash_background_black else R.drawable.splash_background_white
    val mainGreen = Color(0xFF43A047)

    BackHandler { onBackClick() }

    Box(modifier = Modifier.fillMaxSize()) {
        // LAYER 1: Background Pattern
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // LAYER 2: Scrollable Content
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 100.dp), // Padding horizontal biarkan 0 agar Header Full Width
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Jarak antar item di tengah
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // ITEM 1: Header Toko (Tetap Full Width)
            item(span = { GridItemSpan(2) }) {
                Column {
                    StoreProfileHeader(
                        storeName = uiState.outlet?.name ?: "Loading...",
                        location = uiState.outlet?.location ?: "Indonesia",
                        description = stringResource(R.string.store_description),
                        isDark = isDarkActive
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    StoreCategoryFilter(
                        selectedFilter = uiState.selectedCategoryFilter,
                        onFilterSelect = viewModel::onCategoryFilterClicked,
                        isDark = isDarkActive
                    )
                }
            }

            // ITEM 2: Daftar Produk
            val filteredProducts = if (uiState.selectedCategoryFilter == "Semua") {
                uiState.outletProducts
            } else {
                uiState.outletProducts.filter {
                    it.categoryName.contains(
                        uiState.selectedCategoryFilter,
                        true
                    )
                }
            }

            if (uiState.isLoading) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = mainGreen)
                    }
                }
            } else if (filteredProducts.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada produk di kategori ini", color = Color.Gray)
                    }
                }
            } else {
                itemsIndexed(filteredProducts) { index, product ->
                    // Logika: Item Kiri (Genap) butuh padding Kiri. Item Kanan (Ganjil) butuh padding Kanan.
                    val isLeftItem = index % 2 == 0
                    val paddingModifier = if (isLeftItem) {
                        Modifier.padding(start = 16.dp) // Jarak dari pinggir layar kiri
                    } else {
                        Modifier.padding(end = 16.dp)   // Jarak dari pinggir layar kanan
                    }

                    Box(modifier = paddingModifier) {
                        ProductCardItem(product = product, onClick = onProductClick)
                    }
                }

            }
        }

        // LAYER 3: Sticky Header Top Bar
        StoreHeaderTopBar(
            onBackClick = onBackClick,
            isDark = isDarkActive,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

// --- COMPONENTS ---

@Composable
fun StoreHeaderTopBar(
    onBackClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .glossyEffect(isDark, CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (isDark) Color.White else Color.Black
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .glossyEffect(isDark, RoundedCornerShape(22.dp))
                .clickable { /* Search Logic */ }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = if (isDark) Color.White.copy(0.7f) else Color.Black.copy(0.5f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cari di toko",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Color.White.copy(0.7f) else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .glossyEffect(isDark, CircleShape)
                .clickable { /* Cart */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Cart",
                tint = if (isDark) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun StoreProfileHeader(
    storeName: String,
    location: String,
    description: String,
    isDark: Boolean
) {
    val contentColor = if (isDark) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sayuran),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.1f),
                                    Color.Black.copy(alpha = 0.4f)
                                ),
                                startY = 300f
                            )
                        )
                )
            }
            Spacer(modifier = Modifier.height(60.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glossyContainer(isDark, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StoreAvatar(storeName)

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = storeName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " 4.9 (600 Reviews)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDark) Color.White.copy(0.7f) else Color.Gray
                            )
                        }
                        Text(
                            text = stringResource(R.string.store_open_hours),
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallStoreActionButton(
                            text = stringResource(R.string.store_chat),
                            icon = Icons.AutoMirrored.Outlined.Chat,
                            isDark = isDark
                        )
                        SmallStoreActionButton(
                            text = stringResource(R.string.store_address),
                            icon = Icons.Outlined.LocationOn,
                            isDark = isDark
                        )
                    }
                }
            }
        }
    }

    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = contentColor.copy(alpha = 0.8f),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        lineHeight = 20.sp
    )
}

@Composable
fun SmallStoreActionButton(
    text: String,
    icon: ImageVector,
    isDark: Boolean
) {
    Surface(
        color = if (isDark) Color.White.copy(0.1f) else Color.White.copy(0.7f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, Color.Gray.copy(0.3f)),
        modifier = Modifier
            .height(30.dp)
            .clickable { }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun StoreCategoryFilter(
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    isDark: Boolean
) {
    val categories = listOf("Semua", "Sayur", "Bumbu", "Buah", "Daging", "Sembako")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedFilter
            val containerColor =
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            val contentColor =
                if (isSelected) Color.White else if (isDark) Color.White else Color.Black
            val borderColor =
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(0.4f)

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = containerColor,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.clickable { onFilterSelect(category) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (category != "Semua") {
                        val iconRes = when (category) {
                            "Sayur" -> R.drawable.ic_sayuran
                            "Bumbu" -> R.drawable.ic_bumbu
                            "Buah" -> R.drawable.ic_buah
                            "Daging" -> R.drawable.ic_proteinhewani
                            "Sembako" -> R.drawable.ic_bahanpokok
                            else -> R.drawable.ic_logo
                        }
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = contentColor
                    )
                }
            }
        }
    }
}

// --- HELPER COMPOSABLES (Disalin kesini agar tidak Error Unresolved Reference) ---

@Composable
fun StoreAvatar(storeName: String) {
    val randomSeed = abs(storeName.hashCode())
    val storeIcons = listOf(
        R.drawable.ic_sayuran,
        R.drawable.ic_buah,
        R.drawable.ic_proteinhewani,
        R.drawable.ic_bahanpokok,
        R.drawable.ic_bumbu
    )
    val iconRes = storeIcons[randomSeed % storeIcons.size]
    val bgColors = listOf(
        Color(0xFFE3F2FD), Color(0xFFE8F5E9), Color(0xFFFFF3E0), Color(0xFFF3E5F5)
    )
    val bgColor = bgColors[randomSeed % bgColors.size]

    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, Color.Black.copy(0.05f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Store Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun Modifier.glossyEffect(isDark: Boolean, shape: Shape): Modifier {
    val glassColor = if (isDark) Color.Black.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.85f)
    val borderColor =
        if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.5f)
    return this
        .shadow(elevation = 6.dp, shape = shape, spotColor = Color.Black.copy(alpha = 0.1f))
        .background(glassColor, shape)
        .border(1.dp, borderColor, shape)
        .clip(shape)
}

@Composable
fun Modifier.glossyContainer(isDark: Boolean, shape: Shape): Modifier {
    val glassColor =
        if (isDark) Color(0xFF1E1E1E).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.6f)
    return this
        .shadow(elevation = 8.dp, shape = shape, spotColor = Color.Black.copy(alpha = 0.05f))
        .background(glassColor, shape)
        .border(1.dp, borderColor, shape)
        .clip(shape)
}