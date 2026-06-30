package com.pixless.app.imageprocessing

import android.graphics.Bitmap

interface ImageFilter {
    fun apply(bitmap: Bitmap, settings: PixlessSettings): Bitmap
}