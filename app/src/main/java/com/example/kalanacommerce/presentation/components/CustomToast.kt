package com.example.kalanacommerce.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

// Enum ToastType dengan warna yang lebih modern (Pastel/Vibrant mix)
enum class ToastType(val containerColor: Color, val iconColor: Color, val icon: ImageVector) {
    Success(
        containerColor = Color(0xFF0F9D58), // Google Green
        iconColor = Color.White,
        icon = Icons.Rounded.CheckCircle
    ),
    Error(
        containerColor = Color(0xFFDB4437), // Google Red
        iconColor = Color.White,
        icon = Icons.Rounded.ErrorOutline
    ),
    Info(
        containerColor = Color(0xFF4285F4), // Google Blue
        iconColor = Color.White,
        icon = Icons.Rounded.Info
    )
}

@Composable
fun CustomToast(
    message: String,
    isVisible: Boolean,
    type: ToastType = ToastType.Success,
    onDismiss: () -> Unit
) {
    // Animasi Spring (Membal) agar lebih fluid
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it }, // Muncul dari atas
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy, // Efek membal
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp) // Sedikit diturunkan agar tidak terlalu mepet status bar
            .zIndex(100f) // Pastikan di paling atas (overlay)
    ) {

        // Timer otomatis
        LaunchedEffect(isVisible) {
            if (isVisible) {
                delay(3000)
                onDismiss()
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = type.containerColor,
                shape = RoundedCornerShape(28.dp), // Full rounded corners
                shadowElevation = 6.dp, // Shadow lembut
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentSize()
                    // Fitur: Klik toast untuk menutupnya langsung
                    .clickable { onDismiss() }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Wadah Icon dengan background transparan
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)), // Efek kaca buram di belakang icon
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = type.icon,
                            contentDescription = null,
                            tint = type.iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = message,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        // UBAH BAGIAN INI:
                        maxLines = 3, // Ubah dari 2 menjadi 3 agar muat pesan error panjang
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}