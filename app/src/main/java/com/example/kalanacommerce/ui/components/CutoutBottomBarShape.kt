package com.example.kalanacommerce.ui.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * Bentuk custom BottomAppBar dengan cekungan di tengah untuk FAB.
 */
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

        val cutoutRadius = (fabSizePx / 2f) + fabPaddingPx
        val path = Path().apply {
            moveTo(0f, cornerRadiusPx)
            quadraticBezierTo(0f, 0f, cornerRadiusPx, 0f)

            // Cekungan di tengah
            lineTo((size.width / 2) - cutoutRadius, 0f)
            arcTo(
                rect = Rect(
                    left = (size.width / 2) - cutoutRadius,
                    top = -cutoutRadius,
                    right = (size.width / 2) + cutoutRadius,
                    bottom = cutoutRadius
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )

            lineTo(size.width - cornerRadiusPx, 0f)
            quadraticBezierTo(size.width, 0f, size.width, cornerRadiusPx)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }

        return Outline.Generic(path)
    }
}
