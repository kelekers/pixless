package com.pixless.app.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.MotionEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(
    controller: CameraController,
    modifier: Modifier = Modifier,
    onFrameProcessed: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scope = rememberCoroutineScope()
    var focusPos by remember { mutableStateOf<Offset?>(null) }
    var showReticle by remember { mutableStateOf(false) }

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FIT_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    LaunchedEffect(previewView, lifecycleOwner) {
        controller.startCameraWithPreview(previewView, lifecycleOwner, onFrameProcessed)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = modifier.aspectRatio(3f / 4f)) {
            AndroidView(
                factory = {
                    previewView.setOnTouchListener { view, event ->
                        if (event.action == MotionEvent.ACTION_UP) {
                            focusPos = Offset(event.x, event.y)
                            showReticle = true
                            scope.launch {
                                delay(500)
                                showReticle = false
                            }
                            controller.setFocus(previewView.meteringPointFactory, event.x, event.y)
                            view.performClick()
                        }
                        true
                    }
                    previewView
                },
                modifier = Modifier.matchParentSize()
            )

            if (showReticle && focusPos != null) {
                FocusReticle(offset = focusPos!!)
            }

        }
    }
}