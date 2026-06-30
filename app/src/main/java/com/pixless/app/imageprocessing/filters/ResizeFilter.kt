package com.pixless.app.imageprocessing.filters

import android.graphics.Bitmap
import com.pixless.app.imageprocessing.PixlessSettings
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class ResizeFilter {

    private val minDimension = 1
    private val megapixel = 1_000_000f

    // PROGRESSIVE DOWNSCALE
    fun downscale(image: Bitmap, settings: PixlessSettings): Bitmap {
        val width = image.width
        val height = image.height
        val sourcePixels = max(width * height, minDimension).toFloat()
        val targetPixels = max(settings.targetMp, 0.01f) * megapixel
        val scale = min(1.0f, sqrt(targetPixels / sourcePixels))

        val pixelScale = max(settings.pixelSize, minDimension)
        val targetWidth = max(minDimension, ((width * scale) / pixelScale).roundToInt())
        val targetHeight = max(minDimension, ((height * scale) / pixelScale).roundToInt())

        var currentBitmap = image
        var currentWidth = width
        var currentHeight = height

        while (currentWidth / 2 > targetWidth && currentHeight / 2 > targetHeight) {
            currentWidth /= 2
            currentHeight /= 2
            val nextBitmap = Bitmap.createScaledBitmap(currentBitmap, currentWidth, currentHeight, true)
            // Hindari recycle image asli yang masuk dari parameter
            if (currentBitmap != image) {
                currentBitmap.recycle()
            }
            currentBitmap = nextBitmap
        }

        val finalBitmap = Bitmap.createScaledBitmap(currentBitmap, targetWidth, targetHeight, true)
        if (currentBitmap != image && currentBitmap != finalBitmap) {
            currentBitmap.recycle()
        }

        return finalBitmap
    }

    // UPSCALE NEAREST NEIGHBOR
    fun upscaleNearest(image: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(image, targetWidth, targetHeight, false)
    }
}