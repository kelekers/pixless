package com.pixless.app.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageCapture.OutputFileOptions

class CameraController(
    private val context: Context
) {

    private val cameraProviderFuture =
        ProcessCameraProvider.getInstance(context)

    private val imageCapture =
        ImageCapture.Builder().build()

    private var cameraSelector =
        CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var previewView: PreviewView

    private lateinit var lifecycleOwner: androidx.lifecycle.LifecycleOwner

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: androidx.lifecycle.LifecycleOwner
    ) {
        this.previewView = previewView
        this.lifecycleOwner = lifecycleOwner

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()

            preview.surfaceProvider =
                previewView.surfaceProvider

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(

                lifecycleOwner,

                cameraSelector,

                preview,
                imageCapture

            )

        }, ContextCompat.getMainExecutor(context))

    }

    fun takePhoto(
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {

        val name =
            "Pixless_${System.currentTimeMillis()}"

        val contentValues = ContentValues().apply {

            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                name
            )

            put(
                MediaStore.MediaColumns.MIME_TYPE,
                "image/jpeg"
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "Pictures/Pixless"
                )

            }

        }

        val outputOptions = OutputFileOptions.Builder(

            context.contentResolver,

            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

            contentValues

        ).build()

        imageCapture.takePicture(

            outputOptions,

            ContextCompat.getMainExecutor(context),

            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {

                    onSuccess()

                }

                override fun onError(
                    exception: ImageCaptureException
                ) {

                    onError(exception)

                }

            }

        )

    }

    fun switchCamera() {

        cameraSelector =

            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)

                CameraSelector.DEFAULT_FRONT_CAMERA

            else

                CameraSelector.DEFAULT_BACK_CAMERA

        startCamera(

            previewView,

            lifecycleOwner

        )

    }
}

