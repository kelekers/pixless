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
import androidx.camera.view.PreviewView
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalLifecycleOwner

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(
    controller: CameraController
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {

        PreviewView(context)

    }

    AndroidView(

        factory = {

            previewView

        },

        modifier = Modifier.fillMaxSize(),

        update = {

            controller.startCamera(

                previewView,

                lifecycleOwner

            )

        }

    )

}