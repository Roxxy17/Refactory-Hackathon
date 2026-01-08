package com.example.kalanacommerce.presentation.screen.dashboard.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.kalanacommerce.domain.model.Order
import com.example.kalanacommerce.domain.model.OrderStatus
import com.example.kalanacommerce.presentation.screen.dashboard.product.glossyEffect
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: OrderHistoryViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onNavigateToDetail: (String) -> Unit
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
    val contentColor = if (isDarkActive) Color.White else Color.Black
    val mainGreen = Color(0xFF43A047)

    Box(modifier = modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Title
            Text(
                text = stringResource(R.string.history_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = mainGreen,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            )

            // Tabs
            val tabs = listOf(
                stringResource(R.string.history_tab_process),
                stringResource(R.string.history_tab_completed),
                stringResource(R.string.history_tab_cancelled)
            )

            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Color.Transparent,
                contentColor = mainGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[uiState.selectedTab])
                            .height(3.dp),
                        color = mainGreen
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.onTabSelected(index) },
                        text = {
                            Text(
                                text = title,
                                color = if (uiState.selectedTab == index) mainGreen else if(isDarkActive) Color.Gray else Color.DarkGray,
                                fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // List Orders
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = mainGreen)
                }
            } else if (uiState.filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.history_empty),
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(uiState.filteredOrders) { order ->
                        OrderCardItem(
                            order = order,
                            isDark = isDarkActive,
                            onClick = { onNavigateToDetail(order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCardItem(
    order: Order,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val statusColor = when (order.status) {
        OrderStatus.PENDING -> Color(0xFFFF9800) // Orange
        OrderStatus.PAID, OrderStatus.PROCESSED, OrderStatus.SHIPPED -> Color(0xFF2196F3) // Blue
        OrderStatus.COMPLETED -> Color(0xFF4CAF50) // Green
        OrderStatus.CANCELLED -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }

    val statusText = when(order.status) {
        OrderStatus.PENDING -> "Menunggu Bayar"
        OrderStatus.PAID -> "Dibayar"
        OrderStatus.PROCESSED -> "Diproses"
        OrderStatus.SHIPPED -> "Dikirim"
        OrderStatus.COMPLETED -> "Selesai"
        OrderStatus.CANCELLED -> "Dibatalkan"
        else -> "Unknown"
    }

    // Menggunakan Glossy Style Box
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Toko Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logo), // Placeholder icon toko
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.outletName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else Color.Black
                    )
                }

                // Badge Status
                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, statusColor.copy(0.3f))
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tanggal & Order Code
            Text(
                text = "${order.date} • ${order.orderCode}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.Gray.copy(0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            // Total Info & Button
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "${order.itemCount} item",
                        color = if(isDark) Color.White.copy(0.7f) else Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = String.format(Locale("id", "ID"), "Rp %,d", order.totalAmount),
                        color = if (isDark) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                if (order.status == OrderStatus.COMPLETED) {
                    Button(
                        onClick = { /* Logic Beli Lagi */ },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            stringResource(R.string.history_buy_again),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}