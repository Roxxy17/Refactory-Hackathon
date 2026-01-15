package com.example.kalanacommerce.presentation.screen.dashboard.chat

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.ChatMessage
import com.example.kalanacommerce.presentation.components.LoginRequiredView
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

// --- Palet Warna (Adaptive) ---
val KalanaGreen = Color(0xFF00AA13)
val DiscountRed = Color(0xFFFF5722)

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel(),
    themeSetting: ThemeSetting, // [FIX 1] Terima setting tema
    onBackClick: () -> Unit,
    isLoggedIn: Boolean,
    onNavigateToLogin : () -> Unit
) {
    // 1. CEK LOGIN
    if (!isLoggedIn) {
        Box(modifier = Modifier.fillMaxSize()) {
            LoginRequiredView(
                themeSetting = themeSetting,
                onLoginClick = onNavigateToLogin,
                message = stringResource(R.string.login_req_chat_msg)
            )
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        return
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var messageInput by remember { mutableStateOf(TextFieldValue("")) }
    val messages = uiState.messages

    // [FIX 2] Logika Tema: Prioritaskan ThemeSetting, fallback ke System
    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    // Background Image Resource
    val backgroundImageRes = if (isDarkActive) {
        R.drawable.splash_background_black
    } else {
        R.drawable.splash_background_white
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Layer Background
        Image(
            painter = painterResource(id = backgroundImageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Layer Konten
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ChatHeaderSimple(onBackClick = onBackClick, isDarkTheme = isDarkActive)
            },
            bottomBar = {
                ChatInputArea(
                    message = messageInput,
                    onMessageChange = { messageInput = it },
                    onSendClick = {
                        if (messageInput.text.isNotBlank() && !uiState.isLoading) {
                            viewModel.sendMessage(messageInput.text)
                            messageInput = TextFieldValue("")
                        }
                    },
                    isSending = uiState.isLoading,
                    isDarkTheme = isDarkActive
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                reverseLayout = true,
                contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp)
            ) {
                items(messages.reversed()) { msg ->
                    ChatBubbleItem(message = msg, isDarkTheme = isDarkActive)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// =======================================================
// HEADER (Adaptive)
// =======================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeaderSimple(onBackClick: () -> Unit, isDarkTheme: Boolean) {
    val headerContainerColor = if (isDarkTheme) {
        Color(0xFF121212).copy(alpha = 0.9f)
    } else {
        Color.White.copy(alpha = 0.95f)
    }

    val contentColor = if (isDarkTheme) Color.White else Color.Black

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = KalanaGreen,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    // [FIX 3] Extract String
                    Text(
                        text = stringResource(R.string.chat_assistant_name),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = contentColor
                    )
                    Text(
                        text = stringResource(R.string.chat_status_online),
                        style = MaterialTheme.typography.labelSmall,
                        color = KalanaGreen
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.chat_back_content_desc),
                    tint = contentColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = headerContainerColor),
        modifier = Modifier.shadow(elevation = 2.dp)
    )
}

// =======================================================
// CHAT BUBBLE (Adaptive)
// =======================================================
@Composable
fun ChatBubbleItem(message: ChatMessage, isDarkTheme: Boolean) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        // --- AVATAR AI (Kiri) ---
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.size(36.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo), // Pastikan resource ini ada
                    contentDescription = stringResource(R.string.chat_bot_avatar_desc),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(2.dp).clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // --- KONTEN BUBBLE ---
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (message.text.isNotBlank()) {
                val bubbleColor = if (isUser) {
                    KalanaGreen
                } else {
                    if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
                }

                val textColor = if (isUser) {
                    Color.White
                } else {
                    if (isDarkTheme) Color(0xFFEEEEEE) else Color.Black
                }

                val shape = if (isUser) {
                    RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
                } else {
                    RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
                }

                Surface(
                    color = bubbleColor,
                    shape = shape,
                    shadowElevation = if (isUser) 0.dp else 1.dp,
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 20.sp,
                                fontSize = 15.sp
                            ),
                            color = textColor
                        )
                    }
                }
            }

            // --- REKOMENDASI PRODUK ---
            if (!message.isUser && message.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.widthIn(max = 290.dp)
                ) {
                    message.recommendations.forEach { product ->
                        VerticalProductCard(product, isDarkTheme)
                    }
                }
            }
        }
    }
}

// =======================================================
// KARTU PRODUK VERTIKAL
// =======================================================
@Composable
fun VerticalProductCard(product: com.example.kalanacommerce.domain.model.Product, isDarkTheme: Boolean) {
    val cardColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val borderColor = if (isDarkTheme) Color.Gray.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.5f)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { /* Handle Detail */ }
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- GAMBAR ---
            Box(modifier = Modifier.size(72.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.image ?: "")
                        .crossfade(true)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )

                Surface(
                    color = DiscountRed,
                    shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "20%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // --- INFO ---
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name ?: "Produk",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
                    ) {
                        Text(
                            text = product.variantName ?: "Satuan",
                            fontSize = 9.sp,
                            color = Color(0xFFFF9800),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                val price = product.price ?: 0
                val originalPrice = (product.price ?: 0) + 2000

                Text(
                    text = "Rp ${String.format(Locale("id", "ID"), "%,d", price)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rp ${String.format(Locale("id", "ID"), "%,d", originalPrice)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = stringResource(R.string.chat_product_buy_action),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = KalanaGreen,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

// =======================================================
// INPUT AREA (Adaptive)
// =======================================================
@Composable
fun ChatInputArea(
    message: TextFieldValue,
    onMessageChange: (TextFieldValue) -> Unit,
    onSendClick: () -> Unit,
    isSending: Boolean,
    isDarkTheme: Boolean
) {
    val containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val inputFieldColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFF5F5F5)
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Surface(
        color = containerColor,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message,
                onValueChange = onMessageChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.chat_input_placeholder),
                        color = if(isDarkTheme) Color.Gray else Color.LightGray
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = inputFieldColor,
                    unfocusedContainerColor = inputFieldColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = KalanaGreen
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 50.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = onSendClick,
                enabled = message.text.isNotBlank() && !isSending,
                modifier = Modifier.size(48.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = KalanaGreen,
                        strokeWidth = 2.dp
                    )
                } else {
                    Surface(
                        color = if (message.text.isNotBlank()) KalanaGreen else Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = stringResource(R.string.chat_send_content_desc),
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}