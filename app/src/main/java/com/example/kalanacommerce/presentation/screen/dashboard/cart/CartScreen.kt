package com.example.kalanacommerce.presentation.screen.dashboard.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.presentation.screen.dashboard.product.QuantityStepper
import com.example.kalanacommerce.presentation.screen.dashboard.product.glossyContainer
import com.example.kalanacommerce.presentation.screen.dashboard.product.glossyEffect
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun CartScreen(
    viewModel: CartViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onNavigateToCheckout: (String) -> Unit // Mengirim URL/Token ke halaman payment
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- Setup Tema ---
    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }
    val backgroundImage = if (isDarkActive) R.drawable.splash_background_black else R.drawable.splash_background_white
    val mainGreen = Color(0xFF43A047)
    val contentColor = if (isDarkActive) Color.White else Color.Black

    // Navigasi Otomatis jika Checkout Sukses
    LaunchedEffect(uiState.checkoutResult) {
        uiState.checkoutResult?.let { result ->
            // Arahkan ke Payment Screen atau Webview dengan membawa URL/Token
            // Disini kita kirim Token Snap atau Redirect URL
            onNavigateToCheckout(result.snapToken)
            viewModel.onCheckoutHandled()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // --- HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(R.string.cart_title),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = mainGreen
                )
            }

            // --- CONTENT ---
            if (uiState.isLoading && uiState.cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = mainGreen)
                }
            } else if (uiState.cartItems.isEmpty()) {
                EmptyCartView(isDarkActive)
            } else {
                // List Items
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 120.dp), // Ruang untuk Bottom Bar
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    // Header Pilih Semua
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleSelectAll() }
                                .padding(vertical = 8.dp)
                        ) {
                            val isAllSelected = uiState.selectedItemIds.size == uiState.cartItems.size
                            Checkbox(
                                checked = isAllSelected,
                                onCheckedChange = { viewModel.toggleSelectAll() },
                                colors = CheckboxDefaults.colors(checkedColor = mainGreen)
                            )
                            Text(
                                text = stringResource(R.string.cart_select_all),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = contentColor
                            )
                        }
                    }

                    items(uiState.cartItems, key = { it.id }) { item ->
                        CartItemCard(
                            item = item,
                            isSelected = uiState.selectedItemIds.contains(item.id),
                            isDark = isDarkActive,
                            mainColor = mainGreen,
                            onToggleSelect = { viewModel.toggleSelection(item.id) },
                            onIncrement = { viewModel.incrementQuantity(item.id, item.quantity) },
                            onDecrement = { viewModel.decrementQuantity(item.id, item.quantity) },
                            onDelete = { viewModel.deleteItem(item.id) }
                        )
                    }
                }
            }
        }

        // --- BOTTOM BAR (Checkout) ---
        if (uiState.cartItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            ) {
                CheckoutBottomBar(
                    totalPrice = uiState.selectedTotalPrice,
                    selectedCount = uiState.selectedItemIds.size,
                    isLoading = uiState.isCheckoutLoading,
                    isDark = isDarkActive,
                    onCheckout = { viewModel.processCheckout() }
                )
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    isSelected: Boolean,
    isDark: Boolean,
    mainColor: Color,
    onToggleSelect: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit
) {
    val contentColor = if (isDark) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyContainer(isDark, RoundedCornerShape(16.dp))
            .clickable { onToggleSelect() } // Klik card = select
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(checkedColor = mainColor, uncheckedColor = Color.Gray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Image
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(80.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(item.productImage).crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info & Controls
            Column(modifier = Modifier.weight(1f)) {
                // Nama Toko (Opsional, kecil di atas)
                Text(
                    text = item.outletName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.variantName != "-" && item.variantName.isNotEmpty()) {
                    Text(
                        text = item.variantName,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format(Locale("id", "ID"), "Rp %,d", item.price),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = mainColor
                    )

                    // Stepper (Menggunakan komponen reusable yang sudah ada)
                    QuantityStepper(
                        quantity = item.quantity,
                        onIncrement = onIncrement,
                        onDecrement = onDecrement,
                        mainColor = mainColor
                    )
                }
            }

            // Delete Icon (Absolute Top Right of Card content)
            // Bisa ditaruh di Row, atau menggunakan Box di luar
        }

        // Tombol Hapus (Pojok Kanan Atas Overlay)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .clickable { onDelete() }
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete",
                tint = Color.Gray.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun CheckoutBottomBar(
    totalPrice: Long,
    selectedCount: Int,
    isLoading: Boolean,
    isDark: Boolean,
    onCheckout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyContainer(isDark, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.cart_total),
                    style = MaterialTheme.typography.labelMedium,
                    color = if(isDark) Color.White.copy(0.7f) else Color.Gray
                )
                Text(
                    text = String.format(Locale("id", "ID"), "Rp %,d", totalPrice),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onCheckout,
                enabled = selectedCount > 0 && !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = stringResource(R.string.cart_checkout, selectedCount),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCartView(isDark: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingBag,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.Gray.copy(0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.cart_empty),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) Color.White else Color.Black
        )
        Text(
            text = stringResource(R.string.cart_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}