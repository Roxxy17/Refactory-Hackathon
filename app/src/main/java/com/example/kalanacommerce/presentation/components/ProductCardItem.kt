package com.example.kalanacommerce.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.domain.model.Product
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductCardItem(
    product: Product,
    onClick: (String) -> Unit,
    // [UPDATE] Callback sekarang menerima (Product, Int) untuk jumlah
    onQuickAddToCart: (Product, Int) -> Unit = { _, _ -> },
    onQuickBuyNow: (Product, Int) -> Unit = { _, _ -> },
) {
    // State untuk kontrol Dialog
    var showActionDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(product.id) }
    ) {
        Column {
            // --- IMAGE SECTION ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (product.image.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(product.image).crossfade(true).build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Tag Fresh
                Surface(
                    color = Color(0xFF4CAF50),
                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = stringResource(R.string.product_tag_fresh),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                // Tag Diskon
                if (product.discountPercentage > 0) {
                    Surface(
                        color = Color(0xFFFF9800),
                        shape = RoundedCornerShape(bottomStart = 12.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "${product.discountPercentage}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // --- INFO SECTION ---
            Column(modifier = Modifier.padding(12.dp)) {
                // Nama & Varian
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
                    // Varian Chip
                    Surface(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Transparent
                    ) {
                        Text(
                            text = product.variants.firstOrNull()?.name ?: product.variantName.ifEmpty { "Satuan" },
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Harga
                val displayPrice = product.variants.firstOrNull()?.price ?: product.price
                val displayOriginalPrice = product.variants.firstOrNull()?.originalPrice ?: product.originalPrice

                Text(
                    text = formatCurrency(displayPrice),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (product.discountPercentage > 0 && displayOriginalPrice != null) {
                    Text(
                        text = formatCurrency(displayOriginalPrice),
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer (Freshness & Button)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.product_freshness_label, product.freshness),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        // Indikator Panah
                        val totalIndicators = 15
                        val activeIndicators = ((product.freshness / 100f) * totalIndicators).toInt().coerceIn(0, totalIndicators)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-8).dp),
                            modifier = Modifier.padding(top = 2.dp).offset(x = (-3).dp)
                        ) {
                            repeat(activeIndicators) {
                                Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            }
                            repeat(totalIndicators - activeIndicators) {
                                Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    // Tombol Plus -> Dialog
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { showActionDialog = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }

    // --- POP-UP DIALOG ---
    if (showActionDialog) {
        EnhancedQuickActionDialog(
            product = product,
            variantName = product.variants.firstOrNull()?.name ?: product.variantName,
            price = product.variants.firstOrNull()?.price ?: product.price,
            onDismiss = { showActionDialog = false },
            onAddToCart = { qty ->
                showActionDialog = false
                onQuickAddToCart(product, qty)
            },
            onBuyNow = { qty ->
                showActionDialog = false
                onQuickBuyNow(product, qty)
            }
        )
    }
}

@Composable
fun EnhancedQuickActionDialog(
    product: Product,
    variantName: String,
    price: Long,
    onDismiss: () -> Unit,
    onAddToCart: (Int) -> Unit,
    onBuyNow: (Int) -> Unit
) {
    // State Jumlah (Default 1)
    var quantity by remember { mutableIntStateOf(1) }
    val totalPrice = price * quantity

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header: Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Atur Jumlah",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info Produk Ringkas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Placeholder Image / Icon (Opsional, disini pakai Text saja biar rapi)
                    Column {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Varian: ${variantName.ifEmpty { "Standar" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrency(price),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)

                // Selector Jumlah (Quantity)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Jumlah Pembelian",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Counter Widget
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        color = Color.Transparent
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier.size(32.dp),
                                enabled = quantity > 1
                            ) {
                                Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
                            }

                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            IconButton(
                                onClick = { quantity++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Subtotal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = formatCurrency(totalPrice),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tombol Keranjang
                    OutlinedButton(
                        onClick = { onAddToCart(quantity) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(18.dp))
                    }

                    // Tombol Beli
                    Button(
                        onClick = { onBuyNow(quantity) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Beli Sekarang", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

// Helper Format Rupiah Sederhana
fun formatCurrency(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}