package com.pixless.app.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraGrid() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val w = size.width
        val h = size.height

        if (w > 0 && h > 0) {
            val lineColor = Color.White.copy(alpha = 0.35f)
            val stroke = 1.dp.toPx()

            drawLine(lineColor, Offset(w / 3f, 0f), Offset(w / 3f, h), stroke)
            drawLine(lineColor, Offset(2 * w / 3f, 0f), Offset(2 * w / 3f, h), stroke)
            drawLine(lineColor, Offset(0f, h / 3f), Offset(w, h / 3f), stroke)
            drawLine(lineColor, Offset(0f, 2 * h / 3f), Offset(w, 2 * h / 3f), stroke)
        }
    }
}