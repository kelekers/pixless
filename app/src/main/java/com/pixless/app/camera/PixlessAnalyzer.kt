package com.pixless.app.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import android.graphics.Bitmap
import android.graphics.Matrix
import com.pixless.app.imageprocessing.PixlessProcessor
import com.pixless.app.imageprocessing.PixlessSettings

@OptIn(ExperimentalGetImage::class)
class PixlessAnalyzer(
    private val onFrameProcessed: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private val processor = PixlessProcessor()
    private val settings = PixlessSettings(targetMp = 0.05f)

    private var lastAnalyzedTimestamp = 0L
    private val frameRateLimitMs = 1000L / 15L

    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()

        if (currentTimestamp - lastAnalyzedTimestamp < frameRateLimitMs) {
            imageProxy.close()
            return
        }
        lastAnalyzedTimestamp = currentTimestamp

        val rotation = imageProxy.imageInfo.rotationDegrees.toFloat()
        val bitmap = imageProxy.toBitmap()

        val matrix = Matrix().apply { postRotate(rotation) }
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val processed = processor.processLivePreview(rotatedBitmap, settings)

        onFrameProcessed(processed)

        rotatedBitmap.recycle()
        imageProxy.close()
    }
}