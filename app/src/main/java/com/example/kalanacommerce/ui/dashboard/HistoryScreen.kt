package com.example.kalanacommerce.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kalanacommerce.data.model.OrderResponse
import com.example.kalanacommerce.ui.viewmodel.OrderViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    // ✅ INJEKSI ORDER VIEW MODEL
    viewModel: OrderViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allOrders = uiState.orders

    LaunchedEffect(Unit) {
        // Panggil hanya sekali saat Composable memasuki Composition.
        // Ini adalah tempat terbaik untuk memuat data riwayat yang terautentikasi.
        viewModel.loadMyOrders()
    }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dalam Proses", "Selesai", "Terjadwal")

    val filteredOrders = remember(selectedTab, allOrders) {
        when (selectedTab) {
            0 -> allOrders.filter {
                it.status == "pending" || it.status == "paid" || it.status == "shipped"
            }
            1 -> allOrders.filter {
                it.status == "delivered"
            }
            2 -> allOrders.filter {
                it.status == "cancelled" || it.status == "failed"
            }
            else -> emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🟢 Judul
        Text(
            text = "Belanjaan",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = Color(0xFF007F5F)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔄 Tab pilihan
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFFE6F5EF),
            contentColor = Color(0xFF007F5F),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(3.dp),
                    color = Color(0xFF00A676)
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTab == index) Color(0xFF007F5F) else Color.Gray,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📦 Daftar pesanan
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredOrders, key = { it.id }) { order ->
                RealOrderCard(order) // Menggunakan OrderResponse
            }

        }
    }
}

@Composable
fun RealOrderCard(order: OrderResponse) {
    // Helper untuk memformat status (misal: "pending" menjadi "Pending")
    val formattedStatus = order.status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val primaryColor = Color(0xFF007F5F)
    val secondaryColor = Color(0xFF00A676)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "ID: ${order.id}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = primaryColor
            )

            Spacer(modifier = Modifier.height(4.dp))
            // Tampilkan hanya tanggal
            Text(order.created_at.substring(0, 10), color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Items Count tidak tersedia di OrderResponse, jadi kita hanya menampilkan total harga dan status

            Text(
                "Total: Rp ${"%.2f".format(order.total_amount)}", // Format mata uang
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Status: $formattedStatus",
                color = secondaryColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* TODO: Aksi untuk Beli Lagi / Lihat Detail */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = secondaryColor
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text("Beli Lagi", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}