package com.pixless.app.imageprocessing.filters

import android.graphics.Bitmap
import com.pixless.app.imageprocessing.ImageFilter
import com.pixless.app.imageprocessing.PixlessSettings

class SharpenFilter : ImageFilter {

    // APPLY FILTER
    override fun apply(bitmap: Bitmap, settings: PixlessSettings): Bitmap {
        if (settings.sharpen <= 0f) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        val resultPixels = IntArray(width * height)

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        System.arraycopy(pixels, 0, resultPixels, 0, pixels.size)

        val amount = settings.sharpen
        val centerWeight = 1.0f + 4.0f * amount
        val sideWeight = -amount

        // 3x3 CONVOLUTION PASS
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val i = y * width + x

                var rSum = 0.0f
                var gSum = 0.0f
                var bSum = 0.0f

                val offsets = intArrayOf(-width, -1, 0, 1, width)
                val weights = floatArrayOf(sideWeight, sideWeight, centerWeight, sideWeight, sideWeight)

                for (k in 0..4) {
                    val color = pixels[i + offsets[k]]
                    val weight = weights[k]

                    rSum += ((color shr 16) and 0xFF) * weight
                    gSum += ((color shr 8) and 0xFF) * weight
                    bSum += (color and 0xFF) * weight
                }

                val a = pixels[i] and -0x1000000
                val r = rSum.toInt().coerceIn(0, 255)
                val g = gSum.toInt().coerceIn(0, 255)
                val b = bSum.toInt().coerceIn(0, 255)

                resultPixels[i] = a or (r shl 16) or (g shl 8) or b
            }
        }

        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        resultBitmap.setPixels(resultPixels, 0, width, 0, 0, width, height)
        return resultBitmap
    }
}