package com.pixless.app.imageprocessing.filters

import android.graphics.Bitmap
import com.pixless.app.imageprocessing.ImageFilter
import com.pixless.app.imageprocessing.PixlessSettings
import kotlin.math.abs

class CrosshatchFilter : ImageFilter {

    // CONSTANTS
    private val baseHatchSpacing = 8
    private val denseHatchSpacing = 6
    private val maxDarken = 0.72f

    // APPLY FILTER
    override fun apply(bitmap: Bitmap, settings: PixlessSettings): Bitmap {
        if (settings.crosshatch <= 0f) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val luminance = FloatArray(width * height)

        // LUMINANCE PASS
        for (i in pixels.indices) {
            val color = pixels[i]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF
            luminance[i] = (0.299f * r + 0.587f * g + 0.114f * b) / 255.0f
        }

        val darkenFactor = 1.0f - (settings.crosshatch.coerceAtMost(1.0f) * maxDarken)

        // CROSSHATCH PASS
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val i = y * width + x
                val lum = luminance[i]
                val shadow = 1.0f - lum

                if (shadow <= 0.24f) continue

                val gradX = abs(luminance[i + 1] - luminance[i - 1])
                val gradY = abs(luminance[i + width] - luminance[i - width])
                val verticalEdgeBias = gradX > gradY

                val softDiagonal = ((x + y) % baseHatchSpacing == 0) && (shadow > 0.24f)
                val counterDiagonal = ((x - y) % baseHatchSpacing == 0) && (shadow > 0.42f)
                val denseShadow = ((x + y + 4) % denseHatchSpacing == 0) && (shadow > 0.62f)
                val edgeGuided = (x % denseHatchSpacing == 0) && verticalEdgeBias && (shadow > 0.36f)

                if (softDiagonal || counterDiagonal || denseShadow || edgeGuided) {
                    val color = pixels[i]
                    val a = color and -0x1000000
                    var r = (color shr 16) and 0xFF
                    var g = (color shr 8) and 0xFF
                    var b = color and 0xFF

                    r = (r * darkenFactor).toInt().coerceIn(0, 255)
                    g = (g * darkenFactor).toInt().coerceIn(0, 255)
                    b = (b * darkenFactor).toInt().coerceIn(0, 255)

                    pixels[i] = a or (r shl 16) or (g shl 8) or b
                }
            }
        }

        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        resultBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return resultBitmap
    }
}