package com.pixless.app.imageprocessing

import android.graphics.Color

val PIXLESS_PALETTE = intArrayOf(
    Color.rgb(18, 16, 28),
    Color.rgb(42, 38, 52),
    Color.rgb(61, 72, 74),
    Color.rgb(82, 101, 91),
    Color.rgb(121, 132, 105),
    Color.rgb(173, 180, 134),
    Color.rgb(214, 217, 166),
    Color.rgb(239, 235, 194)
)

data class PixlessSettings(
    val targetMp: Float = 0.2f,
    val pixelSize: Int = 2,
    val blur: Float = 0.0f,
    val ditherMode: String = "Bayer 4x4",
    val ditherStrength: Float = 15.0f,
    val crosshatch: Float = 0.1f,
    val sharpen: Float = 0.1f
)