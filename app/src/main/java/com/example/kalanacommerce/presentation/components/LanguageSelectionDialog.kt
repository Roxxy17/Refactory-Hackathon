package com.example.kalanacommerce.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kalanacommerce.R

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // --- HEADER DIALOG ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.language_dialog_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close_content_description),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.language_dialog_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- DAFTAR PILIHAN BAHASA ---
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageOptionItem(
                        label = stringResource(R.string.bahasa_indonesia),
                        description = stringResource(R.string.language_indonesia_desc),
                        flag = "🇮🇩",
                        isSelected = currentLanguage == "id",
                        onSelect = { onLanguageSelected("id") }
                    )
                    LanguageOptionItem(
                        label = stringResource(R.string.english),
                        description = stringResource(R.string.language_english_desc),
                        flag = "🇺🇸",
                        isSelected = currentLanguage == "en",
                        onSelect = { onLanguageSelected("en") }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionItem(
    label: String,
    description: String,
    flag: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    // Animasi warna yang sama dengan ThemeSelection
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300), label = "langBgBtn"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 300), label = "langBorderBtn"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indikator Bendera (Menggantikan Icon)
            Text(
                text = flag,
                fontSize = 28.sp,
                modifier = Modifier.width(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Teks Label dan Deskripsi
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Menggunakan SelectionIndicator yang sama dengan ThemeSelectionDialog
            LanguageSelectionIndicator(isSelected = isSelected, primaryColor = primaryColor)
        }
    }
}

@Composable
private fun LanguageSelectionIndicator(isSelected: Boolean, primaryColor: Color) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else Color.Transparent,
        label = "langIndicatorBg"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else MaterialTheme.colorScheme.outlineVariant,
        label = "langIndicatorBorder"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(animatedBgColor)
            .border(2.dp, animatedBorderColor, CircleShape)
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.selected_content_description),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}