package com.example.kalanacommerce.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * Shape untuk BottomAppBar yang memiliki lekukan ke atas (convex) di tengah.
 * Shape ini tidak lagi "melubangi" bar, melainkan membentuk tonjolan ke atas.
 */
fun convexBottomBarShape(
    fabSize: Dp,
    fabPadding: Dp,
    cornerRadius: Dp
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

            val cutoutRadius = (fabSizePx / 2f) + fabPaddingPx
            val cutoutHeight = cutoutRadius
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
