package com.example.kalanacommerce.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.example.kalanacommerce.R
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kalanacommerce.data.local.datastore.ThemeSetting

@Composable
fun ThemeSelectionDialog(
    currentSetting: ThemeSetting,
    onDismiss: () -> Unit,
    onSelectTheme: (ThemeSetting) -> Unit
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
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.theme_dialog_title), // Menggunakan Resource
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close_content_description), // Menggunakan Resource
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.theme_dialog_subtitle), // Menggunakan Resource
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ThemeOptionItem(
                        setting = ThemeSetting.SYSTEM,
                        currentSetting = currentSetting,
                        icon = Icons.Outlined.BrightnessAuto,
                        label = stringResource(R.string.theme_option_system),
                        description = stringResource(R.string.theme_option_system_desc),
                        onSelect = onSelectTheme
                    )
                    ThemeOptionItem(
                        setting = ThemeSetting.LIGHT,
                        currentSetting = currentSetting,
                        icon = Icons.Outlined.LightMode,
                        label = stringResource(R.string.theme_option_light),
                        description = stringResource(R.string.theme_option_light_desc),
                        onSelect = onSelectTheme
                    )
                    ThemeOptionItem(
                        setting = ThemeSetting.DARK,
                        currentSetting = currentSetting,
                        icon = Icons.Outlined.DarkMode,
                        label = stringResource(R.string.theme_option_dark),
                        description = stringResource(R.string.theme_option_dark_desc),
                        onSelect = onSelectTheme
                    )
                }
            }
        }
    }
}

/**
 * Sub-komponen untuk item pilihan tema tunggal (Kartu Pilihan).
 */
@Composable
private fun ThemeOptionItem(
    setting: ThemeSetting,
    currentSetting: ThemeSetting,
    icon: ImageVector,
    label: String,
    description: String,
    onSelect: (ThemeSetting) -> Unit
) {
    val isSelected = setting == currentSetting
    val primaryColor = MaterialTheme.colorScheme.primary

    // Animasi warna background dan border saat dipilih
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300), label = "bgColorBtn"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 300), label = "borderColorBtn"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect(setting) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Tema di sebelah kiri
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
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
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Indikator Seleksi (Checkmark Kustom) pengganti RadioButton
            SelectionIndicator(isSelected = isSelected, primaryColor = primaryColor)
        }
    }
}

@Composable
private fun SelectionIndicator(isSelected: Boolean, primaryColor: Color) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else Color.Transparent,
        label = "indicatorBg"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else MaterialTheme.colorScheme.outlineVariant,
        label = "indicatorBorder"
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