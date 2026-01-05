package com.example.kalanacommerce.presentation.screen.dashboard.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.presentation.components.CustomToast // Pastikan import ini ada
import com.example.kalanacommerce.presentation.components.PullToRefreshWrapper
import com.example.kalanacommerce.presentation.components.ToastType
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    themeSetting: ThemeSetting
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

    // --- LISTENER PESAN DARI VIEWMODEL ---
    LaunchedEffect(uiState.error, uiState.successMessage) {
        // Handle Error
        if (uiState.error != null) {
            toastMessage = uiState.error!!
            toastType = ToastType.Error
            showToast = true
            viewModel.clearMessages()
        }

        // Handle Success (Untuk Refresh)
        if (uiState.successMessage != null) {
            toastMessage = uiState.successMessage!!
            toastType = ToastType.Success
            showToast = true
            viewModel.clearMessages()
        }
    }

    val bgImage = if (isDarkActive) R.drawable.background_home_black else R.drawable.background_home_white

    // ROOT BOX
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. STATIC BACKGROUND
        Image(
            painter = painterResource(id = bgImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. MAIN CONTENT
        Scaffold(
            containerColor = Color.Transparent,
        ) { paddingValues ->
            // Wrapper dipasang DI DALAM Scaffold, membungkus LazyColumn
            PullToRefreshWrapper(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refreshData() },
                modifier = Modifier.padding(paddingValues) // Padding Scaffold masuk ke sini
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
                                text = "Belanja Bahan\nMasakan Tanpa\nKeluar Rumah",
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

                    // --- SECTION 4: CATEGORY ROW (STICKY GLOSSY BAR) ---
                    stickyHeader {
                        Spacer(modifier = Modifier.height(16.dp))
                        StickyCategoryRow(
                            categories = uiState.categories,
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
                                text = "Promo Bulan Ini",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // --- SECTION 6: PRODUCT GRID ---
                    if (uiState.isLoading && !uiState.isRefreshing) { // Loading awal
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
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
                                        ProductCardItem(product = product)
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

        // [PENTING: TAMBAHAN LOGIC UI TOAST DI SINI]
        // Ini dirender di luar Scaffold agar menumpuk di atas segalanya (Z-Index tinggi)
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
    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
        content()
    }
}

// --- HELPER: THICK GLOSSY EFFECT ---
@Composable
fun thickGlossyModifier(
    isDark: Boolean,
    shape: androidx.compose.ui.graphics.Shape
): Modifier {
    val glassColor = if (isDark)
        Color.Black.copy(alpha = 0.75f)
    else
        Color.White.copy(alpha = 0.90f)

    val borderColor = if (isDark)
        Color.White.copy(alpha = 0.15f)
    else
        Color.White.copy(alpha = 0.5f)

    return Modifier
        .shadow(
            elevation = 6.dp,
            shape = shape,
            spotColor = Color.Black.copy(alpha = 0.1f)
        )
        .background(glassColor, shape)
        .border(1.dp, borderColor, shape)
        .clip(shape)
}

// --- STICKY 1: SEARCH BAR ---
@Composable
fun StickySearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    isDark: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Search Input
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
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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

            // Cart Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .then(thickGlossyModifier(isDark, CircleShape))
                    .clickable { /* Cart Action */ }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// --- STICKY 2: CATEGORY ROW ---
@Composable
fun StickyCategoryRow(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelect: (String) -> Unit,
    isDark: Boolean
) {
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
                items(categories) { category ->
                    WhatsAppStylePill(
                        category = category,
                        isSelected = category.id == selectedCategoryId,
                        onSelect = { onCategorySelect(category.id) }
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
        targetValue = if (isSelected) primaryColor else Color.Transparent,
        label = "pillBg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else primaryColor,
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
            Icon(
                imageVector = getIconByCategoryName(category.name),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = contentColor
                    )
                }
            }
        }
    }
}

fun getIconByCategoryName(name: String): ImageVector {
    return when {
        name.contains("Sayur", true) -> Icons.Default.Eco
        name.contains("Buah", true) -> Icons.Default.LocalFlorist
        name.contains("Daging", true) -> Icons.Default.SetMeal
        name.contains("Bumbu", true) -> Icons.Default.SoupKitchen
        name.contains("Snack", true) -> Icons.Default.Cookie
        name.contains("Semua", true) -> Icons.Default.GridView
        else -> Icons.Default.Category
    }
}

// --- BANNER CAROUSEL ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCarousel(isDark: Boolean) {
    val pagerState = rememberPagerState(pageCount = { 4 })

    LaunchedEffect(pagerState.currentPage) {
        delay(3000)
        var newPage = pagerState.currentPage + 1
        if (newPage >= pagerState.pageCount) newPage = 0
        pagerState.animateScrollToPage(newPage)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp
        ) { page ->
            val imageRes = when(page) {
                0 -> R.drawable.slide_1
                1 -> R.drawable.slide_4
                2 -> R.drawable.slide_1
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
                        if(isDark) Color.White.copy(alpha=0.2f) else Color.White,
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            repeat(pagerState.pageCount) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    if(isDark) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.2f)

                val width by animateDpAsState(targetValue = if (isSelected) 32.dp else 12.dp, label = "width")

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

// --- PRODUCT CARD ---
@Composable
fun ProductCardItem(product: Product) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Detail Product */ }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (product.image.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.image)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Surface(
                    color = Color(0xFF4CAF50),
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Fresh",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = Color(0xFFFF9800),
                    shape = RoundedCornerShape(bottomStart = 12.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "20%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Transparent
                    ) {
                        Text(
                            text = "250 gr",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = String.format(Locale("id", "ID"), "Rp%,d", product.price),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = String.format(Locale("id", "ID"), "Rp%,d", product.price + 2000),
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kesegaran 78%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(modifier = Modifier.padding(top = 2.dp)) {
                            repeat(8) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 2.dp)
                                        .size(width = 4.dp, height = 4.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                )
                            }
                            repeat(2) {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 2.dp)
                                        .size(width = 4.dp, height = 4.dp)
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha=0.3f), CircleShape)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}