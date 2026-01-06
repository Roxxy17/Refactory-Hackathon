package com.example.kalanacommerce.presentation.screen.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.remote.dto.chat.ChatProductRecommendation
import org.koin.androidx.compose.koinViewModel
import java.util.Locale
import kotlin.collections.reversed
import androidx.compose.foundation.lazy.items
import com.example.kalanacommerce.presentation.screen.dashboard.chat.ChatMessage
import com.example.kalanacommerce.presentation.screen.dashboard.chat.ChatViewModel

// Palet Warna
val GojekGreen = Color(0xFF00AA13)
val SenderBubbleColor = Color(0xFFE5F8E8)
val UserBubbleColor = Color(0xFFFFFFFF)
val BackgroundColor = Color(0xFFF6F6F6)

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var messageInput by remember { mutableStateOf(TextFieldValue("")) }
    val messages = uiState.messages

    Scaffold(
        topBar = {
            ChatHeaderGojek(
                contactName = "Kalana Assistant",
                status = "Online",
                profileImage = R.drawable.ic_chat, // Pastikan ada drawable ini atau ganti
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            ChatInputGojek(
                message = messageInput,
                onMessageChange = { messageInput = it },
                onSendClick = {
                    if (messageInput.text.isNotBlank() && !uiState.isLoading) {
                        viewModel.sendMessage(messageInput.text)
                        messageInput = TextFieldValue("")
                    }
                },
                isSending = uiState.isLoading
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            reverseLayout = true,
            contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp)
        ) {
            // Gunakan List 'messages.reversed()' langsung
            // Pastikan sudah 'import androidx.compose.foundation.lazy.items' di atas
            items(messages.reversed()) { msg ->
                // Sekarang 'msg' adalah ChatMessage, bukan Int
                ChatBubbleGojek(message = msg)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// =======================================================
// CHAT BUBBLE (Support Product Cards)
// =======================================================
@Composable
fun ChatBubbleGojek(message: ChatMessage) {
    val bubbleColor = if (message.isUser) UserBubbleColor else SenderBubbleColor
    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        // 1. Teks Pesan
        if (message.text.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = alignment
            ) {
                Surface(
                    color = bubbleColor,
                    shape = shape,
                    shadowElevation = 1.dp,
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // 2. Kartu Produk (Jika Ada) - Hanya muncul di sisi AI (Sender)
        if (!message.isUser && message.recommendations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rekomendasi Produk:",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            // Horizontal Scroll untuk produk
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(message.recommendations) { product ->
                    ChatProductCard(product)
                }
            }
        }
    }
}

// =======================================================
// PRODUCT CARD COMPONENT
// =======================================================
@Composable
fun ChatProductCard(product: ChatProductRecommendation) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(140.dp)
            .clickable { /* Handle klik produk ke detail */ }
    ) {
        Column {
            // Gambar Produk
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.image ?: "") // URL Gambar
                    .crossfade(true)
                    .placeholder(R.drawable.ic_sayur) // Placeholder error
                    .error(R.drawable.ic_sayur)
                    .build(),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.LightGray)
            )

            // Info Produk
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name ?: "Produk",
                    maxLines = 2,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp,
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Format Harga
                val formattedPrice = String.format(Locale("id", "ID"), "Rp%,d", product.price ?: 0)
                Text(
                    text = formattedPrice,
                    fontSize = 12.sp,
                    color = GojekGreen,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Add (Kecil)
                Button(
                    onClick = { /* Handle Add to Cart */ },
                    colors = ButtonDefaults.buttonColors(containerColor = GojekGreen),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Beli", fontSize = 11.sp)
                }
            }
        }
    }
}

// =======================================================
// HEADER & INPUT (Sama seperti sebelumnya dengan sedikit penyesuaian)
// =======================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeaderGojek(contactName: String, status: String, profileImage: Int, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = profileImage),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(contactName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(status, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GojekGreen)
    )
}

@Composable
fun ChatInputGojek(
    message: TextFieldValue,
    onMessageChange: (TextFieldValue) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean
) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                placeholder = { Text("Tanya asisten...", fontSize = 14.sp, color = Color.Gray) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GojekGreen,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color(0xFFF9F9F9),
                    unfocusedContainerColor = Color(0xFFF9F9F9)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSendClick,
                enabled = message.text.isNotBlank() && !isSending,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (message.text.isNotBlank()) GojekGreen else Color.LightGray)
            ) {
                if (isSending) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, null, tint = Color.White)
                }
            }
        }
    }
}