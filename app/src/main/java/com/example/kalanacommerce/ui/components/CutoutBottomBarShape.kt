package com.example.kalanacommerce.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.sqrt

fun cutoutBottomBarShape(
    fabSize: Dp,
    fabPadding: Dp,
    cornerRadius: Dp
): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val fabSizePx = with(density) { fabSize.toPx() }
        val fabPaddingPx = with(density) { fabPadding.toPx() }
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }

        // Radius dari cekungan, termasuk padding
        val cutoutRadius = (fabSizePx / 2f) + fabPaddingPx
        // Seberapa dalam cekungan
        val cutoutHeight = cutoutRadius
        // Seberapa lebar "handle" kurva bezier untuk transisi yang mulus
        val handleWidth = cutoutRadius / 1.5f

        val path = Path().apply {
            // Mulai dari pojok kiri atas (setelah radius)
            moveTo(0f, cornerRadiusPx)

            // Kurva pojok kiri atas
            quadraticBezierTo(0f, 0f, cornerRadiusPx, 0f)

            // Garis lurus menuju awal transisi cekungan
            lineTo((size.width / 2) - cutoutRadius - handleWidth, 0f)

            // --- INI BAGIAN YANG DIPERBAIKI ---
            // Kurva transisi pertama yang "mencelup" dengan mulus ke dalam cekungan
            cubicTo(
                x1 = (size.width / 2) - cutoutRadius,
                y1 = 0f, // Titik kontrol 1
                x2 = (size.width / 2) - cutoutRadius,
                y2 = cutoutHeight, // Titik kontrol 2
                x3 = size.width / 2,
                y3 = cutoutHeight  // Titik akhir (dasar cekungan)
            )

            // Kurva transisi kedua yang "naik" dengan mulus dari cekungan
            cubicTo(
                x1 = (size.width / 2) + cutoutRadius,
                y1 = cutoutHeight, // Titik kontrol 1
                x2 = (size.width / 2) + cutoutRadius,
                y2 = 0f, // Titik kontrol 2
                x3 = (size.width / 2) + cutoutRadius + handleWidth,
                y3 = 0f // Titik akhir (kembali ke garis datar)
            )
            // ------------------------------------

            // Garis lurus ke pojok kanan atas
            lineTo(size.width - cornerRadiusPx, 0f)

            // Kurva pojok kanan atas
            quadraticBezierTo(size.width, 0f, size.width, cornerRadiusPx)

            // Selesaikan path ke bawah dan tutup
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}
