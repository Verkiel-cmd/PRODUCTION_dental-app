package com.example.dental

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CurvedBottomBarShape(
    private val cutoutRadius: Float = 130f,
    private val cornerRadius: Float = 30f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val cx = size.width / 2f
            val r = cutoutRadius
            val dipDepth = r * 1.2f

            // Start top-left corner
            moveTo(0f, cornerRadius)
            quadraticTo(0f, 0f, cornerRadius, 0f)

            // Line to left side of the center dip
            lineTo(cx - r * 1.4f, 0f)

            // Smooth curve down into the dip
            cubicTo(
                cx - r * 0.9f, 0f,
                cx - r * 0.8f, dipDepth,
                cx, dipDepth
            )

            // Smooth curve back up to the right side
            cubicTo(
                cx + r * 0.8f, dipDepth,
                cx + r * 0.9f, 0f,
                cx + r * 1.4f, 0f
            )

            // Line to top-right corner
            lineTo(size.width - cornerRadius, 0f)
            quadraticTo(size.width, 0f, size.width, cornerRadius)

            // Bottom-right, bottom-left, close
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}