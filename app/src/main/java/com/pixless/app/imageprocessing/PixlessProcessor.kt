package com.pixless.app.imageprocessing

import android.graphics.Bitmap
import com.pixless.app.imageprocessing.filters.CrosshatchFilter
import com.pixless.app.imageprocessing.filters.DitheringFilter
import com.pixless.app.imageprocessing.filters.QuantizeFilter
import com.pixless.app.imageprocessing.filters.ResizeFilter
import com.pixless.app.imageprocessing.filters.SharpenFilter
import com.pixless.app.imageprocessing.filters.BlurFilter

class PixlessProcessor {

    private val resizeFilter = ResizeFilter()
    private val ditheringFilter = DitheringFilter()
    private val quantizeFilter = QuantizeFilter()
    private val crosshatchFilter = CrosshatchFilter()
    private val sharpenFilter = SharpenFilter()
    private val blurFilter = BlurFilter()

    fun processImage(image: Bitmap, settings: PixlessSettings = PixlessSettings(), isPreview: Boolean = false): Bitmap {
        var finalWidth = image.width
        var finalHeight = image.height

        val maxDimension = 1440
        if (!isPreview && Math.max(finalWidth, finalHeight) > maxDimension) {
            val scale = maxDimension.toFloat() / Math.max(finalWidth, finalHeight)
            finalWidth = (finalWidth * scale).toInt()
            finalHeight = (finalHeight * scale).toInt()
        }

        var workingBitmap = resizeFilter.downscale(image, settings)
        workingBitmap = blurFilter.apply(workingBitmap, settings)
        workingBitmap = ditheringFilter.apply(workingBitmap, settings)
        workingBitmap = quantizeFilter.apply(workingBitmap, settings)

        if (!isPreview) {
            workingBitmap = crosshatchFilter.apply(workingBitmap, settings)
        }

        workingBitmap = resizeFilter.upscaleNearest(workingBitmap, finalWidth, finalHeight)

        if (!isPreview) {
            workingBitmap = sharpenFilter.apply(workingBitmap, settings)
        }

        return workingBitmap
    }

    fun processLivePreview(image: Bitmap, settings: PixlessSettings): Bitmap {
        val originalWidth = image.width
        val originalHeight = image.height

        val previewWidth = 120
        val previewHeight = (previewWidth * (originalHeight.toFloat() / originalWidth.toFloat())).toInt()

        var workingBitmap = Bitmap.createScaledBitmap(image, previewWidth, previewHeight, false)

        workingBitmap = quantizeFilter.apply(workingBitmap, settings)

        val finalBitmap = resizeFilter.upscaleNearest(workingBitmap, originalWidth, originalHeight)

        if (workingBitmap != image && workingBitmap != finalBitmap) {
            workingBitmap.recycle()
        }

        return finalBitmap
    }
}