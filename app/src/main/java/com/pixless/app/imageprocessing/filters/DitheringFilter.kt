package com.pixless.app.imageprocessing.filters

import android.graphics.Bitmap
import com.pixless.app.imageprocessing.ImageFilter
import com.pixless.app.imageprocessing.PixlessSettings

class DitheringFilter : ImageFilter {

    // MATRICES
    private val bayer2 = floatArrayOf(
        0f, 2f,
        3f, 1f
    ).map { it / 4f }.toFloatArray()

    private val bayer4 = floatArrayOf(
        0f, 8f, 2f, 10f,
        12f, 4f, 14f, 6f,
        3f, 11f, 1f, 9f,
        15f, 7f, 13f, 5f
    ).map { it / 16f }.toFloatArray()

    private val bayer8 = floatArrayOf(
        0f, 48f, 12f, 60f, 3f, 51f, 15f, 63f,
        32f, 16f, 44f, 28f, 35f, 19f, 47f, 31f,
        8f, 56f, 4f, 52f, 11f, 59f, 7f, 55f,
        40f, 24f, 36f, 20f, 43f, 27f, 39f, 23f,
        2f, 50f, 14f, 62f, 1f, 49f, 13f, 61f,
        34f, 18f, 46f, 30f, 33f, 17f, 45f, 29f,
        10f, 58f, 6f, 54f, 9f, 57f, 5f, 53f,
        42f, 26f, 38f, 22f, 41f, 25f, 37f, 21f
    ).map { it / 64f }.toFloatArray()

    // APPLY FILTER
    override fun apply(bitmap: Bitmap, settings: PixlessSettings): Bitmap {
        if (settings.ditherMode == "None" || settings.ditherStrength <= 0f) {
            return bitmap
        }

        val (matrix, matrixSize) = when (settings.ditherMode) {
            "Bayer 2x2" -> Pair(bayer2, 2)
            "Bayer 4x4" -> Pair(bayer4, 4)
            "Bayer 8x8" -> Pair(bayer8, 8)
            else -> return bitmap
        }

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // DITHERING PASS
        for (y in 0 until height) {
            for (x in 0 until width) {
                val mx = x % matrixSize
                val my = y % matrixSize
                val threshold = matrix[my * matrixSize + mx]
                val offset = (threshold - 0.5f) * settings.ditherStrength

                val index = y * width + x
                val color = pixels[index]
                val a = color and -0x1000000
                var r = (color shr 16) and 0xFF
                var g = (color shr 8) and 0xFF
                var b = color and 0xFF

                r = (r + offset).toInt().coerceIn(0, 255)
                g = (g + offset).toInt().coerceIn(0, 255)
                b = (b + offset).toInt().coerceIn(0, 255)

                pixels[index] = a or (r shl 16) or (g shl 8) or b
            }
        }

        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        resultBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return resultBitmap
    }
}