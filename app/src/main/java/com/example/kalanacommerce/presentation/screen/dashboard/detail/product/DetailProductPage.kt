package com.example.kalanacommerce.presentation.screen.dashboard.detail.product

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.ProductVariant
import com.example.kalanacommerce.presentation.components.ProductCardItem
import org.koin.androidx.compose.koinViewModel
import java.util.Locale
import kotlin.math.abs

@Composable
fun DetailProductPage(
    productId: String,
    viewModel: DetailProductViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit = {},
    onStoreClick: (String) -> Unit = {},
    onNavigateToCheckout: (String) -> Unit,
    onNavigateToCart: () -> Unit
) {
    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    LaunchedEffect(uiState.addToCartSuccessMessage, uiState.error, uiState.navigateToCheckoutWithId) {
        uiState.addToCartSuccessMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
        uiState.error?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
        uiState.navigateToCheckoutWithId?.let { itemId ->
            onNavigateToCheckout(itemId)
            viewModel.onMessageShown()
        }
    }

    val backgroundImage = if (isDarkActive) R.drawable.splash_background_black else R.drawable.splash_background_white
    val contentColor = if (isDarkActive) Color.White else Color.Black
    val mainGreen = Color(0xFF43A047)

    BackHandler { onBackClick() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            // [MODIFIKASI PENTING] Set WindowInsets ke 0 agar konten (gambar) bisa nembus ke status bar
            contentWindowInsets = WindowInsets(0.dp)
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = mainGreen
                    )
                } else if (uiState.product != null) {
                    val product = uiState.product!!
                    val currentPrice = uiState.selectedVariant?.price ?: product.price
                    val currentOriginalPrice = uiState.selectedVariant?.originalPrice ?: product.originalPrice

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            // [MODIFIKASI PENTING] Jangan gunakan paddingValues penuh.
                            // Hanya ambil padding bawah (untuk navigasi bar), biarkan atasnya 0.
                            .padding(bottom = paddingValues.calculateBottomPadding())
                            .verticalScroll(rememberScrollState())
                    ) {
                        // --- 1. Product Image Header ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .shadow(
                                    elevation = 24.dp,
                                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                                    spotColor = Color.Black.copy(alpha = 0.5f)
                                )
                                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                                .background(Color.White)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(product.image)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = product.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                            startY = 500f
                                        )
                                    )
                            )

                            if (product.variants.isNotEmpty()) {
                                VariantSelector(
                                    variants = product.variants,
                                    selectedVariant = uiState.selectedVariant,
                                    onVariantSelected = viewModel::onVariantSelected,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 16.dp, bottom = 24.dp)
                                )
                            }
                        }

                        // --- 2. Content Body ---
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            // Title & Tag
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    color = contentColor,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .glossyEffect(isDarkActive, RoundedCornerShape(8.dp))
                                        .border(1.dp, mainGreen, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.product_tag_fresh),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = mainGreen
                                    )
                                }
                            }

                            // Rating
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9800),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "4.8",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = contentColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.detail_reviews, "100"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = contentColor.copy(alpha = 0.7f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Harga & Stepper
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glossyContainer(isDarkActive, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = String.format(Locale("id", "ID"), "Rp %,d", currentPrice),
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = mainGreen
                                        )
                                    )
                                    if (currentOriginalPrice != null && currentOriginalPrice > currentPrice) {
                                        Text(
                                            text = String.format(Locale("id", "ID"), "Rp %,d", currentOriginalPrice),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                textDecoration = TextDecoration.LineThrough,
                                                color = contentColor.copy(alpha = 0.6f)
                                            )
                                        )
                                    }
                                }

                                QuantityStepper(
                                    quantity = uiState.quantity,
                                    onIncrement = viewModel::incrementQuantity,
                                    onDecrement = viewModel::decrementQuantity,
                                    mainColor = mainGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Deskripsi
                            Text(
                                text = stringResource(R.string.detail_description),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 22.sp,
                                    color = contentColor.copy(alpha = 0.85f)
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Store Card
                            val storeName = product.outlet?.name ?: "Toko Kalana"
                            StoreCardSection(
                                storeName = storeName,
                                rating = "4.9/5.0",
                                isDark = isDarkActive,
                                onStoreClick = {
                                    val outletId = product.outlet?.id ?: ""
                                    if (outletId.isNotEmpty()) {
                                        onStoreClick(outletId)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Produk Lain
                            Text(
                                text = stringResource(R.string.detail_other_products),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Grid Produk Lain
                            val related = uiState.relatedProducts
                            if (related.isNotEmpty()) {
                                val chunked = related.chunked(2)
                                chunked.forEach { rowItems ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                                    ) {
                                        rowItems.forEach { relatedItem ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                ProductCardItem(
                                                    product = relatedItem,
                                                    onClick = onProductClick
                                                )
                                            }
                                        }
                                        if (rowItems.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            } else {
                                Text("Tidak ada produk terkait", color = contentColor.copy(0.5f))
                            }

                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }

        // LAYER 3: Sticky Header Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp), // Top 48.dp memberi ruang agar tidak tertutup jam/sinyal
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .glossyEffect(isDarkActive, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDarkActive) Color.White else Color.Black
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .glossyEffect(isDarkActive, CircleShape)
                    .clickable { onNavigateToCart() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart",
                    tint = if (isDarkActive) Color.White else Color.Black
                )
            }
        }

        // LAYER 4: FLOATING BOTTOM ACTION BAR
        if (uiState.product != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                BottomActionSection(
                    price = uiState.totalPrice,
                    isDark = isDarkActive,
                    isLoading = uiState.isAddToCartLoading,
                    onAddToCart = { viewModel.addToCart() },
                    onBuyNow = { viewModel.buyNow() }
                )
            }
        }
    }
}

// --- COMPONENTS ---

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomActionSection(
    price: Long,
    isDark: Boolean,
    isLoading: Boolean,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyContainer(isDark, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(0.35f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = if (isDark) Color.White.copy(0.6f) else Color.Gray
                )
                AnimatedContent(
                    targetState = price,
                    transitionSpec = { slideInVertically { it } togetherWith slideOutVertically { -it } },
                    label = "PriceAnimation"
                ) { targetPrice ->
                    Text(
                        text = String.format(Locale("id", "ID"), "Rp%,d", targetPrice),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier.weight(0.65f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = { if (!isLoading) onAddToCart() },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .height(44.dp)
                        .weight(0.3f)
                        .glossyEffect(isDark, RoundedCornerShape(12.dp))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add Cart",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Button(
                    onClick = { if (!isLoading) onBuyNow() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .weight(0.7f)
                        .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = MaterialTheme.colorScheme.primary.copy(0.5f))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(
                            text = stringResource(R.string.detail_buy_now),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoreCardSection(storeName: String, rating: String, isDark: Boolean, onStoreClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glossyContainer(isDark, RoundedCornerShape(16.dp))
            .clickable { onStoreClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StoreAvatar(storeName)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = storeName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) Color.White else Color.Black
            )
            Text(
                text = rating,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) Color.White.copy(0.7f) else Color.Gray
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.Login,
            contentDescription = null,
            tint = Color(0xFF43A047),
            modifier = Modifier.size(24.dp)
        )
    }
}

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
    val bgColors = listOf(Color(0xFFE3F2FD), Color(0xFFE8F5E9), Color(0xFFFFF3E0), Color(0xFFF3E5F5))
    val bgColor = bgColors[randomSeed % bgColors.size]

    Box(
        modifier = Modifier
            .size(48.dp)
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
                .padding(8.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun VariantSelector(
    variants: List<ProductVariant>,
    selectedVariant: ProductVariant?,
    onVariantSelected: (ProductVariant) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .shadow(8.dp, CircleShape)
            .background(Color.White, CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(variants) { variant ->
            val isSelected = selectedVariant?.id == variant.id
            val bgColor = if (isSelected) Color(0xFF43A047) else Color.Transparent
            val textColor = if (isSelected) Color.White else Color.Black

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(bgColor)
                    .clickable { onVariantSelected(variant) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = variant.name,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    mainColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(mainColor, RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onDecrement() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .background(Color.White, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .heightIn(min = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onIncrement() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun Modifier.glossyEffect(isDark: Boolean, shape: Shape): Modifier {
    val glassColor = if (isDark) Color.Black.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.85f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.5f)
    return this
        .shadow(elevation = 6.dp, shape = shape, spotColor = Color.Black.copy(alpha = 0.1f))
        .background(glassColor, shape)
        .border(1.dp, borderColor, shape)
        .clip(shape)
}

@Composable
fun Modifier.glossyContainer(isDark: Boolean, shape: Shape): Modifier {
    val glassColor = if (isDark) Color(0xFF1E1E1E).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.6f)
    return this
        .shadow(elevation = 8.dp, shape = shape, spotColor = Color.Black.copy(alpha = 0.05f))
        .background(glassColor, shape)
        .border(1.dp, borderColor, shape)
        .clip(shape)
}