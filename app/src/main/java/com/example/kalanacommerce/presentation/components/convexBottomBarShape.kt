package com.example.kalanacommerce.presentation.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp // Pastikan dp diimpor

/**
 * Shape untuk BottomAppBar yang memiliki lekukan ke atas (convex) di tengah.
 * Shape ini tidak lagi "melubangi" bar, melainkan membentuk tonjolan ke atas.
 *
 * @param fabSize Ukuran FAB.
 * @param fabPadding Jarak antara FAB dan lekukan.
 * @param cornerRadius Radius sudut untuk bar.
 * @param extraHeight Ketinggian ekstra untuk membuat tonjolan lebih tinggi.
 */
fun convexBottomBarShape(
    fabSize: Dp,
    fabPadding: Dp,
    cornerRadius: Dp,
    // --- PERUBAHAN 1: Tambahkan parameter ini ---
    extraHeight: Dp = 0.dp
): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val fabSizePx = with(density) { fabSize.toPx() }
            val fabPaddingPx = with(density) { fabPadding.toPx() }
            val cornerRadiusPx = with(density) { cornerRadius.toPx() }
            // --- PERUBAHAN 2: Konversi extraHeight ke Px ---
            val extraHeightPx = with(density) { extraHeight.toPx() }

            val cutoutRadius = (fabSizePx / 2f) + fabPaddingPx
            // --- PERUBAHAN 3: Tambahkan extraHeightPx ke cutoutHeight ---
            val cutoutHeight = cutoutRadius + extraHeightPx
            // Kontrol untuk kelembutan kurva transisi
            val handleWidth = cutoutRadius / 1.5f

            // Mulai dari pojok kiri bawah
            moveTo(0f, size.height)
            // Garis ke pojok kanan bawah
            lineTo(size.width, size.height)
            // Garis ke pojok kanan atas
            lineTo(size.width, cornerRadiusPx)
            // Kurva pojok kanan atas
            quadraticBezierTo(size.width, 0f, size.width - cornerRadiusPx, 0f)

            // Garis lurus menuju awal transisi lekukan (dari kanan)
            lineTo((size.width / 2) + cutoutRadius + handleWidth, 0f)

            // Kurva transisi "naik" dari lekukan
            cubicTo(
                x1 = (size.width / 2) + cutoutRadius, y1 = 0f,
                x2 = (size.width / 2) + cutoutRadius, y2 = cutoutHeight,
                x3 = size.width / 2, y3 = cutoutHeight
            )

            // Kurva transisi "turun" ke lekukan
            cubicTo(
                x1 = (size.width / 2) - cutoutRadius, y1 = cutoutHeight,
                x2 = (size.width / 2) - cutoutRadius, y2 = 0f,
                x3 = (size.width / 2) - cutoutRadius - handleWidth, y3 = 0f
            )

            // Garis lurus ke pojok kiri atas
            lineTo(cornerRadiusPx, 0f)
            // Kurva pojok kiri atas
            quadraticBezierTo(0f, 0f, 0f, cornerRadiusPx)

            // Tutup path
            close()
        }
        return Outline.Generic(path)
    }
}
