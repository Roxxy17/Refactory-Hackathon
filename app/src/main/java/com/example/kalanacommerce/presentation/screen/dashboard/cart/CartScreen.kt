package com.example.kalanacommerce.presentation.screen.dashboard.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.presentation.components.CustomToast
import com.example.kalanacommerce.presentation.components.LoginRequiredView
import com.example.kalanacommerce.presentation.components.PullToRefreshWrapper
import com.example.kalanacommerce.presentation.components.ToastType
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

// --- Custom Colors ---
val BrandOrange = Color(0xFFF96D20)
val BrandGreen = Color(0xFF43A047)
val DarkCardBg = Color(0xFF1E1E1E)
val LightCardBorder = Color(0xFFEEEEEE)
val DarkCardBorder = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    isLoggedIn : Boolean,
    viewModel: CartViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit,
    onNavigateToCheckout: (String) -> Unit,
    onNavigateToDetailProduct: (String) -> Unit,
    onNavigateToStore: (String) -> Unit,
    onNavigateToLogin:() -> Unit,
    onNavigateToHome: () -> Unit
) {
    // 1. CEK LOGIN TERLEBIH DAHULU
    if (!isLoggedIn) {
        // Tampilkan layar Login Required (Background Splash)
        Box(modifier = Modifier.fillMaxSize()) {
            LoginRequiredView(
                themeSetting = themeSetting,
                onLoginClick = onNavigateToLogin,
                message = stringResource(R.string.login_req_cart_msg)
            )
            // Tombol Back tetap dimunculkan agar user bisa kembali
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black // Sesuaikan logic tema manual jika perlu
                )
            }
        }
        return // Hentikan eksekusi ke bawah (Kode Cart Asli tidak dijalankan)
    }
    val uiState by viewModel.uiState.collectAsState()

    // --- STATE UNTUK CUSTOM TOAST ---
    var showToast by remember { mutableStateOf(false) }
    var toastMsg by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.Success) }

    // [LOGIC HITUNG HEMAT DINAMIS - DIPERBAIKI]
    // Menghitung selisih harga asli dan harga jual untuk item yang DIPILIH (Selected)
    val totalSavedAmount = remember(uiState.cartItems, uiState.selectedItemIds) {
        uiState.cartItems
            .filter { it.id in uiState.selectedItemIds }
            .sumOf { item ->
                // [FIX] Handle nullable originalPrice
                val originalPrice = item.originalPrice ?: 0L
                if (originalPrice > item.price) {
                    (originalPrice - item.price) * item.quantity
                } else {
                    0L
                }
            }
    }

    // 1. Handle Error dari ViewModel
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            toastMsg = uiState.successMessage!!
            toastType = ToastType.Success
            showToast = true
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            toastMsg = uiState.error!!
            toastType = ToastType.Error
            showToast = true
            viewModel.clearMessages()
        }
    }

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

    LaunchedEffect(uiState.checkoutResult) {
        uiState.checkoutResult?.let { result ->
            onNavigateToCheckout(result.snapToken)
            viewModel.onCheckoutHandled()
        }
    }

    // ROOT CONTAINER
    Box(modifier = Modifier.fillMaxSize()) {

        // LAYER 1: Background Image
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // LAYER 2: Konten List
        PullToRefreshWrapper(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp)
        ) {
            if (uiState.cartItems.isEmpty() && !uiState.isLoading) {
                EmptyCartView(isDarkActive,onNavigateToHome = {
                    onNavigateToHome()
                    // Nanti di AppNavGraph: navController.navigate(Screen.Dashboard.route) { popUpTo(0) }
                })
            } else {
                val groupedItems = remember(uiState.cartItems) {
                    uiState.cartItems.groupBy { it.outletName }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 100.dp, bottom = 120.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    groupedItems.forEach { (outletName, items) ->
                        item {
                            val currentOutletId = items.firstOrNull()?.outletId.orEmpty()
                            StoreGroupCard(
                                outletName = outletName,
                                items = items,
                                selectedIds = uiState.selectedItemIds,
                                isDark = isDarkActive,
                                onStoreClick = {
                                    if (currentOutletId.isNotEmpty()) {
                                        onNavigateToStore(currentOutletId)
                                    }
                                },
                                onProductClick = { productId -> onNavigateToDetailProduct(productId) },
                                onToggleSelect = { id -> viewModel.toggleSelection(id) },
                                onIncrement = { id, qty -> viewModel.incrementQuantity(id, qty) },
                                onDecrement = { id, qty -> viewModel.decrementQuantity(id, qty) },
                                onDelete = { id -> viewModel.deleteItem(id) }
                            )
                        }
                    }
                }
            }
        }

        // LAYER 3: HEADER MANUAL (Transparan)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Color.Transparent)
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = if (isDarkActive) Color.White else Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Keranjang",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isDarkActive) Color.White else Color.Black
            )
        }

        // LAYER 4: FLOATING CHECKOUT BAR
        if (uiState.cartItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                FloatingCheckoutBar(
                    totalPrice = uiState.selectedTotalPrice,
                    savedAmount = totalSavedAmount,
                    itemCount = uiState.selectedItemIds.size,
                    isLoading = false,
                    onCheckout = {
                        val ids = uiState.selectedItemIds.joinToString(",")
                        if (ids.isNotEmpty()) {
                            onNavigateToCheckout(ids)
                        } else {
                            toastMsg = "Pilih minimal 1 barang"
                            toastType = ToastType.Error
                            showToast = true
                        }
                    }
                )
            }
        }

        // LAYER 5: CUSTOM TOAST
        CustomToast(
            message = toastMsg,
            isVisible = showToast,
            type = toastType,
            onDismiss = { showToast = false }
        )
    }
}

// --- KOMPONEN PENDUKUNG ---

@Composable
fun FloatingCheckoutBar(
    totalPrice: Long,
    savedAmount: Long,
    itemCount: Int,
    isLoading: Boolean,
    onCheckout: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(100.dp),
        colors = CardDefaults.cardColors(containerColor = BrandOrange),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(enabled = itemCount > 0 && !isLoading, onClick = onCheckout)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$itemCount item",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatRupiah(totalPrice),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
                        color = Color.White
                    )
                    if (savedAmount > 0) {
                        Text(
                            text = "Hemat ${formatRupiah(savedAmount)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                    color = Color.White,
                    trackColor = BrandOrange.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun StoreGroupCard(
    outletName: String,
    items: List<CartItem>,
    selectedIds: Set<String>,
    isDark: Boolean,
    onStoreClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onToggleSelect: (String) -> Unit,
    onIncrement: (String, Int) -> Unit,
    onDecrement: (String, Int) -> Unit,
    onDelete: (String) -> Unit
) {
    val isStoreSelected = items.isNotEmpty() && items.all { selectedIds.contains(it.id) }
    val cardBgColor = if (isDark) DarkCardBg else Color.White
    val borderColor = if (isDark) DarkCardBorder else Color.Transparent

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = if (isDark) BorderStroke(1.dp, borderColor) else null,
        elevation = CardDefaults.cardElevation(if (isDark) 4.dp else 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStoreClick() }
                    .padding(start = 12.dp, top = 16.dp, bottom = 12.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isStoreSelected,
                    onCheckedChange = { isChecked ->
                        items.forEach {
                            if (selectedIds.contains(it.id) != isChecked) onToggleSelect(it.id)
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BrandGreen,
                        uncheckedColor = if (isDark) Color.Gray else Color(0xFFE0E0E0),
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = if (isDark) BrandGreen.copy(alpha = 0.8f) else BrandGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = outletName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isDark) Color.Gray else Color.LightGray
                )
            }

            Divider(
                color = if (isDark) Color.White.copy(0.1f) else Color.Black.copy(0.05f),
                thickness = 1.dp
            )

            items.forEachIndexed { index, item ->
                BeautifulCartItem(
                    item = item,
                    isSelected = selectedIds.contains(item.id),
                    isDark = isDark,
                    onContentClick = { onProductClick(item.productId) },
                    onToggleSelect = { onToggleSelect(item.id) },
                    onIncrement = { onIncrement(item.id, item.quantity) },
                    onDecrement = { onDecrement(item.id, item.quantity) },
                    onDelete = { onDelete(item.id) }
                )
                if (index < items.size - 1) {
                    Divider(
                        color = if (isDark) Color.White.copy(0.05f) else Color.Black.copy(0.03f),
                        modifier = Modifier.padding(start = 56.dp, end = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BeautifulCartItem(
    item: CartItem,
    isSelected: Boolean,
    isDark: Boolean,
    onContentClick: () -> Unit,
    onToggleSelect: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color.Black
    val secondaryTextColor = if (isDark) Color.Gray else Color.DarkGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.height(80.dp), contentAlignment = Alignment.Center) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(
                    checkedColor = BrandGreen,
                    uncheckedColor = if (isDark) Color.Gray else Color(0xFFE0E0E0),
                    checkmarkColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color.Black.copy(0.3f) else Color.Gray.copy(0.05f))
                .clickable { onContentClick() }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.productImage)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (item.discountPercentage > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(BrandOrange, RoundedCornerShape(bottomStart = 8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${item.discountPercentage}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = textColor,
                        modifier = Modifier.clickable { onContentClick() }
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (item.variantName != "-") item.variantName else "Satuan",
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryTextColor
                    )
                }

                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus",
                        tint = if (isDark) Color.Gray else Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // [FIX] Handle nullable originalPrice
                    val originalPrice = item.originalPrice ?: 0L

                    if (item.discountPercentage > 0 && originalPrice > item.price) {
                        Text(
                            text = formatRupiah(originalPrice),
                            style = MaterialTheme.typography.labelSmall.copy(textDecoration = TextDecoration.LineThrough),
                            color = secondaryTextColor
                        )
                    }
                    Text(
                        text = formatRupiah(item.price),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BrandOrange
                    )
                }

                QuantityStepperPill(
                    quantity = item.quantity,
                    isDark = isDark,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Segar ${item.freshness}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium, fontSize = 10.sp),
                    color = BrandGreen
                )
            }
        }
    }
}

@Composable
fun QuantityStepperPill(
    quantity: Int,
    isDark: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val borderColor = if (isDark) DarkCardBorder else LightCardBorder
    val iconColor = if (isDark) Color.White else Color.Black
    val bgColor = if(isDark) Color.Black.copy(0.3f) else Color.White

    Surface(
        shape = RoundedCornerShape(50),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.height(32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clickable { onDecrement() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Kurangi",
                    tint = if (quantity > 1) iconColor else iconColor.copy(0.3f),
                    modifier = Modifier.size(14.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if(isDark) Color.White else Color.Black
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(BrandGreen.copy(alpha = 0.1f))
                    .clickable { onIncrement() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah",
                    tint = BrandGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

fun formatRupiah(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

@Composable
fun EmptyCartView(isDark: Boolean, onNavigateToHome: () -> Unit  ) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = if(isDark) BrandGreen.copy(0.3f) else BrandGreen.copy(0.2f),
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.cart_empty_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.cart_empty_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDark) Color.Gray else Color.DarkGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNavigateToHome,
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Mulai Belanja", fontWeight = FontWeight.Bold)
        }
    }
}