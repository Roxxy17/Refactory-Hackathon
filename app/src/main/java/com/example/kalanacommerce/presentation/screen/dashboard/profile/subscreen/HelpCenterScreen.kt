package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.presentation.theme.KalanaCommerceTheme
import androidx.compose.foundation.BorderStroke // <--- Tambahkan ini
import androidx.compose.animation.animateColorAsState // <--- Tambahkan ini

// Data Class untuk FAQ
data class FaqItem(
    val question: String, val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState =
        androidx.compose.foundation.rememberScrollState() // Jika tidak pakai LazyColumn full page

    // --- SETUP ANIMASI BLOB ---
    val blobColor1 = MaterialTheme.colorScheme.primary
    val blobColor2 = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background

    val infiniteTransition = rememberInfiniteTransition(label = "Infinite BG")

    val blob1Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob1"
    )

    val blob2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob2"
    )

    // --- DATA FAQ (Ditambah) ---
    val faqList = listOf(
        FaqItem(stringResource(R.string.faq_q1), stringResource(R.string.faq_a1)),
        FaqItem(stringResource(R.string.faq_q2), stringResource(R.string.faq_a2)),
        FaqItem(stringResource(R.string.faq_q3), stringResource(R.string.faq_a3)),
        FaqItem(stringResource(R.string.faq_q4), stringResource(R.string.faq_a4)),
        // Tambahan
        FaqItem(stringResource(R.string.faq_q5), stringResource(R.string.faq_a5)),
        FaqItem(stringResource(R.string.faq_q6), stringResource(R.string.faq_a6))
    )

    // --- ROOT CONTAINER (BOX) ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- LAYER 1: BACKGROUND ANIMATION (BLOB) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Blob Atas Kiri
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColor1.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f * blob1Scale
                ), center = Offset(0f, 0f), radius = size.width * 0.8f * blob1Scale
            )

            // Blob Bawah Kanan
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColor2.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width - blob2Offset, size.height + blob2Offset),
                radius = size.width * 0.9f
            )
        }

        // --- LAYER 2: KONTEN UTAMA ---
        Scaffold(
            containerColor = Color.Transparent, // PENTING: Agar blob terlihat
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                    Text(
                        stringResource(R.string.pusat_bantuan),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }, navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.kembali),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent // Header transparan
                )
                )
            }) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- HEADER SECTION ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.help_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // --- CONTACT BUTTONS ---
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Tombol WhatsApp
                        ContactCard(
                            modifier = Modifier.weight(1f),
                            iconVector = Icons.Outlined.SupportAgent,
                            title = stringResource(R.string.chat_admin),
                            subtitle = stringResource(R.string.whatsapp),
                            color = Color(0xFF25D366), // Warna WA
                            onClick = {
                                val phoneNumber = "088806147806"
                                val message = "Halo Admin Kalana, saya butuh bantuan."
                                val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${
                                    Uri.encode(message)
                                }"
                                val intent =
                                    Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) { /* Handle error */
                                }
                            })

                        // 2. Tombol Website
                        ContactCard(
                            modifier = Modifier.weight(1f),
                            iconVector = Icons.Outlined.Language,
                            title = stringResource(R.string.visit_us),
                            subtitle = stringResource(R.string.website),
                            color = Color(0xFF2196F3), // Warna Web Biru
                            onClick = {
                                val url = "https://kalanacommerce.com"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            })
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // --- FAQ HEADER ---
                item {
                    Text(
                        text = stringResource(R.string.faq_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // --- FAQ LIST ---
                items(faqList) { item ->
                    FaqItemCard(item)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    modifier: Modifier = Modifier,
    iconVector: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp) // Lebih tinggi sedikit
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Shadow halus
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)) // Border tipis sesuai warna
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Gradient Halus
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.05f), color.copy(alpha = 0.15f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                // Icon Container
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Text
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun FaqItemCard(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    // Animasi warna border saat expanded
    val borderColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(
            alpha = 0.5f
        ), label = "border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f) // Glassy dikit
        ), elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // QUESTION ROW
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = item.question,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    lineHeight = 20.sp
                )

                // Icon ganti antara + dan - agar lebih modern
                Icon(
                    imageVector = if (expanded) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = "Expand",
                    tint = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ANSWER SECTION
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) // Background jawaban agak beda
                            .padding(16.dp)
                    ) {
                        Text(
                            text = item.answer,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpCenterPreview() {
    KalanaCommerceTheme {
        HelpCenterScreen(onNavigateBack = {})
    }
}