package com.example.kalanacommerce.front.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryScreen(modifier: Modifier = Modifier, onBack: () -> Unit = {},

                  ) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Dalam Proses", "Selesai", "Terjadwal")

    val dummyOrders = listOf(
        OrderData("MT-78916742", "08 Oktober 2025 23:44", 2, "15.000", "Dalam Proses"),
        OrderData("MT-78916743", "06 Oktober 2025 20:30", 3, "25.000", "Selesai"),
        OrderData("MT-78916744", "05 Oktober 2025 10:15", 1, "8.000", "Terjadwal")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAF9))
            .padding(16.dp)
    ) {
        // 🟢 Judul
        Text(
            text = "Riwayat Belanja",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = Color(0xFF007F5F)
        )

        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

        // 📦 Daftar pesanan
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(dummyOrders) { order ->
                OrderCard(order)
            }
        }
    }
}

data class OrderData(
    val id: String,
    val date: String,
    val itemsCount: Int,
    val totalPrice: String,
    val status: String
)

@Composable
fun OrderCard(order: OrderData) {
    val statusColor = when (order.status) {
        "Dalam Proses" -> Color(0xFFFFC107)
        "Selesai" -> Color(0xFF4CAF50)
        "Terjadwal" -> Color(0xFF2196F3)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = order.id,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF007F5F)
                )

                // 🔵 Badge status
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(order.date, color = Color.Gray, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("${order.itemsCount} item", color = Color.Gray, fontSize = 13.sp)
                    Text(
                        "Rp ${order.totalPrice}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = {},
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00A676)
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Beli Lagi", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}
