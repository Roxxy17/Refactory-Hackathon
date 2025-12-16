package com.example.kalanacommerce.front.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kalanacommerce.R // Pastikan R.drawable.ic_chat ada
import com.example.kalanacommerce.front.viewmodel.ChatViewModel
import org.koin.androidx.compose.koinViewModel

// --- Palet Warna Gojek ---
val GojekGreen = Color(0xFF00AA13) // Hijau Gojek Asli
val SenderBubbleColor = Color(0xFFE5F8E8) // Hijau sangat muda untuk pesan masuk (lawan bicara)
val UserBubbleColor = Color(0xFFFFFFFF) // Putih untuk pesan keluar (pengguna)
val BackgroundColor = Color(0xFFF6F6F6) // Latar belakang abu-abu muda

// =======================================================
// MAIN CHAT SCREEN
// =======================================================
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel() // 1. Inject ViewModel
) {
    // Ambil state dari ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // State lokal untuk input pesan pengguna
    var messageInput by remember { mutableStateOf(TextFieldValue("")) }

    // Dapatkan daftar pesan dari UI State
    val messages = uiState.messages

    Scaffold(
        topBar = {
            ChatHeaderGojek(
                contactName = "Kalila Atha",
                status = "Online",
                profileImage = R.drawable.ic_chat // Pastikan ini ada
            )
        },
        bottomBar = {
            ChatInputGojek(
                message = messageInput,
                onMessageChange = { messageInput = it },
                onSendClick = {
                    // Logika kirim pesan (misalnya menambahkan ke daftar pesan dan mereset input)
                    if (messageInput.text.isNotBlank() && !uiState.isLoading) {
                        viewModel.sendMessage(messageInput.text) // 2. Panggil ViewModel
                        messageInput = TextFieldValue("") // Bersihkan input
                    }
                },
                isSending = uiState.isLoading // Menampilkan indikator loading jika sedang mengirim
            )
        },
        containerColor = BackgroundColor // Menggunakan warna latar belakang khusus
    ) { paddingValues ->
        // Daftar pesan
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            reverseLayout = true, // Menampilkan pesan terbaru di bawah
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages.reversed()) { msg ->
                ChatBubbleGojek(message = msg)
                Spacer(modifier = Modifier.height(6.dp)) // Jarak antar bubble dikurangi
            }
        }
    }
}

data class Message(val text: String, val isUser: Boolean)

// =======================================================
// CHAT BUBBLE (Ditingkatkan)
// =======================================================
@Composable
fun ChatBubbleGojek(message: Message) {
    val bubbleColor = if (message.isUser) UserBubbleColor else SenderBubbleColor
    val textColor = Color.Black // Teks tetap hitam, lebih bersih
    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start

    // Bentuk bubble yang lebih modern (bulat penuh di sisi yang jauh)
    val shape = if (message.isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = shape,
            // Menggunakan shadow yang lebih halus
            tonalElevation = 1.dp,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 14.sp, // Ukuran teks sedikit lebih kecil
                color = textColor,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

// =======================================================
// HEADER CHAT GOJEK (Ditingkatkan)
// =======================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeaderGojek(contactName: String, status: String, profileImage: Int) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Foto Profil
                Image(
                    painter = painterResource(id = profileImage),
                    contentDescription = "Foto Profil",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                // Detail Kontak
                Column {
                    Text(
                        text = contactName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold // Lebih berani
                    )
                    Text(
                        text = status,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* aksi back */ }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White
                )
            }
        },
        // Menggunakan warna Gojek asli
        colors = TopAppBarDefaults.topAppBarColors(containerColor = GojekGreen),
        // Memberi bayangan agar terangkat
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    )
}

@Composable
fun ChatInputGojek(
    message: TextFieldValue,
    onMessageChange: (TextFieldValue) -> Unit,
    onSendClick: () -> Unit,
    isSending:Boolean
) {
    Surface(
        color = Color.White,
        shadowElevation = 4.dp // Memberi bayangan agar terlihat terpisah dari chat
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Field
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                placeholder = { Text("Ketik pesan...", color = Color.Gray) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp), // Radius lebih besar
                maxLines = 4, // Izinkan lebih dari 1 baris
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GojekGreen.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = GojekGreen,
                    unfocusedContainerColor = Color(0xFFF7F7F7), // Sedikit abu-abu di latar belakang input
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Tombol Kirim
            IconButton(
                onClick = onSendClick,
                enabled = message.text.isNotBlank(), // Nonaktifkan jika kosong
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (message.text.isNotBlank()) GojekGreen else Color.LightGray)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Kirim",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatScreen() {
    ChatScreen()
}