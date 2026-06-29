package com.pixless.app.camera

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onNavigateToGallery: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = remember { CameraController(context) }

    var flashMode by remember { mutableStateOf(CameraController.FlashMode.OFF) }
    var shutter by remember { mutableStateOf(false) }
    var lastPhotoUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        lastPhotoUri = controller.getLastPhotoUri()
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                actions = {
                    // FLASH BUTTON
                    IconButton(
                        onClick = {
                            flashMode = when (flashMode) {
                                CameraController.FlashMode.OFF -> CameraController.FlashMode.ON
                                CameraController.FlashMode.ON -> CameraController.FlashMode.TORCH
                                CameraController.FlashMode.TORCH -> CameraController.FlashMode.OFF
                            }
                            controller.setFlashMode(flashMode)
                        }
                    ) {
                        Icon(
                            imageVector = when (flashMode) {
                                CameraController.FlashMode.OFF -> Icons.Default.FlashOff
                                CameraController.FlashMode.ON -> Icons.Default.FlashOn
                                CameraController.FlashMode.TORCH -> Icons.Default.Highlight
                            },
                            contentDescription = "Flash",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            RequestCameraPermission {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        CameraPreview(
                            controller = controller,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(vertical = 16.dp)
                        )
                        if (shutter) {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(horizontal = 36.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // GALLERY THUMBNAIL
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.DarkGray)
                                .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                                .clickable { onNavigateToGallery() }
                        ) {
                            if (lastPhotoUri != null) {
                                AsyncImage(
                                    model = lastPhotoUri,
                                    contentDescription = "Last Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        // CAPTURE BUTTON
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(Color.White, CircleShape)
                                .clickable {
                                    controller.takePhoto(
                                        onSuccess = {
                                            lastPhotoUri = controller.getLastPhotoUri()
                                        },
                                        onError = {}
                                    )
                                    scope.launch {
                                        shutter = true
                                        delay(100)
                                        shutter = false
                                    }
                                }
                        )

                        // SWITCH CAMERA
                        IconButton(onClick = { controller.switchCamera() }) {
                            Icon(
                                imageVector = Icons.Default.Cached,
                                contentDescription = "Switch Camera",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}