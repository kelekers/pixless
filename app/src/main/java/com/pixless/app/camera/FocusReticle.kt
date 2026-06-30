package com.pixless.app.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun FocusReticle(offset: Offset) {
    val sizePx = 60f
    val halfSize = sizePx / 2f
    val lineLength = 15f

    Canvas(
        modifier = Modifier
            .size(60.dp)
            .offset { IntOffset(offset.x.toInt() - 30, offset.y.toInt() - 30) }
    ) {
        val outlineColor = Color.Black
        val fillColor = Color.White

        drawReticleGeometry(
            color = outlineColor,
            sizePx = sizePx,
            halfSize = halfSize,
            lineLength = lineLength,
            strokeWidth = 8f
        )

        drawReticleGeometry(
            color = fillColor,
            sizePx = sizePx,
            halfSize = halfSize,
            lineLength = lineLength,
            strokeWidth = 4f
        )
    }
}

private fun DrawScope.drawReticleGeometry(
    color: Color,
    sizePx: Float,
    halfSize: Float,
    lineLength: Float,
    strokeWidth: Float
) {
    drawRect(
        color = color,
        size = Size(sizePx, sizePx),
        style = Stroke(width = strokeWidth)
    )

    drawLine(color, Offset(0f, halfSize), Offset(lineLength, halfSize), strokeWidth = strokeWidth)
    drawLine(color, Offset(sizePx - lineLength, halfSize), Offset(sizePx, halfSize), strokeWidth = strokeWidth)
    drawLine(color, Offset(halfSize, 0f), Offset(halfSize, lineLength), strokeWidth = strokeWidth)
    drawLine(color, Offset(halfSize, sizePx - lineLength), Offset(halfSize, sizePx), strokeWidth = strokeWidth)
}