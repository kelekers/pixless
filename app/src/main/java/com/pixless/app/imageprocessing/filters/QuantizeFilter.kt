package com.pixless.app.imageprocessing.filters

import android.graphics.Bitmap
import com.pixless.app.imageprocessing.ImageFilter
import com.pixless.app.imageprocessing.PIXLESS_PALETTE
import com.pixless.app.imageprocessing.PixlessSettings

class QuantizeFilter : ImageFilter {

    // QUANTIZE PIXELS
    override fun apply(bitmap: Bitmap, settings: PixlessSettings): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r1 = (color shr 16) and 0xFF
            val g1 = (color shr 8) and 0xFF
            val b1 = color and 0xFF

            var minDistance = Int.MAX_VALUE
            var nearestColor = PIXLESS_PALETTE[0]

            for (pColor in PIXLESS_PALETTE) {
                val r2 = (pColor shr 16) and 0xFF
                val g2 = (pColor shr 8) and 0xFF
                val b2 = pColor and 0xFF

                val dr = r1 - r2
                val dg = g1 - g2
                val db = b1 - b2
                val dist = (dr * dr) + (dg * dg) + (db * db)

                if (dist < minDistance) {
                    minDistance = dist
                    nearestColor = pColor
                }
            }

            val alpha = color and -0x1000000
            pixels[i] = alpha or (nearestColor and 0x00FFFFFF)
        }

        val resultBitmap = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        resultBitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return resultBitmap
    }
}