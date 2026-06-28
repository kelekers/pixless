package com.pixless.app.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview() {

    val context = LocalContext.current

    val previewView = remember {

        PreviewView(context)

    }

    AndroidView(

        factory = {

            previewView

        },

        modifier = Modifier.fillMaxSize(),

        update = {

            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({

                val cameraProvider =
                    cameraProviderFuture.get()

                val preview =
                    Preview.Builder().build()

                preview.surfaceProvider =
                    previewView.surfaceProvider

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(

                    context as androidx.lifecycle.LifecycleOwner,

                    CameraSelector.DEFAULT_BACK_CAMERA,

                    preview

                )

            }, ContextCompat.getMainExecutor(context))

        }

    )

}