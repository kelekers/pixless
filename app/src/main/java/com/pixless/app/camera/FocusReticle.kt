package com.pixless.app.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun FocusReticle(offset: Offset) {
    Canvas(
        modifier = Modifier
            .size(60.dp)
            .offset { IntOffset(offset.x.toInt() - 30, offset.y.toInt() - 30) }
    ) {
        val color = Color(0xFFEFE8C2)

        drawRect(color, size = Size(60f, 60f), style = Stroke(width = 4f))

        drawLine(color, Offset(0f, 30f), Offset(15f, 30f), strokeWidth = 4f)
        drawLine(color, Offset(45f, 30f), Offset(60f, 30f), strokeWidth = 4f)
        drawLine(color, Offset(30f, 0f), Offset(30f, 15f), strokeWidth = 4f)
        drawLine(color, Offset(30f, 45f), Offset(30f, 60f), strokeWidth = 4f)
    }
}