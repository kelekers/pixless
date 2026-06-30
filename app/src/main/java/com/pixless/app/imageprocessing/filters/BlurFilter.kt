package com.pixless.app.imageprocessing.filters

import android.graphics.Bitmap
import com.pixless.app.imageprocessing.ImageFilter
import com.pixless.app.imageprocessing.PixlessSettings

class BlurFilter : ImageFilter {

    override fun apply(bitmap: Bitmap, settings: PixlessSettings): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        val resultPixels = IntArray(width * height)

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        System.arraycopy(pixels, 0, resultPixels, 0, pixels.size)

        val offsets = intArrayOf(
            -width - 1, -width, -width + 1,
            -1,          0,      1,
            width - 1,  width,  width + 1
        )
        val weights = intArrayOf(
            0, 1, 0,
            1, 8, 1,
            0, 1, 0
        )
        val weightSum = 12

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val i = y * width + x

                var rSum = 0
                var gSum = 0
                var bSum = 0

                for (k in 0..8) {
                    val color = pixels[i + offsets[k]]
                    val weight = weights[k]

                    rSum += ((color shr 16) and 0xFF) * weight
                    gSum += ((color shr 8) and 0xFF) * weight
                    bSum += (color and 0xFF) * weight
                }

                val a = pixels[i] and -0x1000000
                val r = (rSum / weightSum).coerceIn(0, 255)
                val g = (gSum / weightSum).coerceIn(0, 255)
                val b = (bSum / weightSum).coerceIn(0, 255)

                resultPixels[i] = a or (r shl 16) or (g shl 8) or b
            }
        }

        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        resultBitmap.setPixels(resultPixels, 0, width, 0, 0, width, height)
        return resultBitmap
    }
}