package com.pixless.app.camera

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            RequestCameraPermission {

                val context = LocalContext.current
                val controller = remember { CameraController(context) }

                Column(modifier = Modifier.fillMaxSize()) {

                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        CameraPreview(controller = controller)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Spacer(modifier = Modifier.size(48.dp))

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable {
                                    controller.takePhoto(
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Photo Saved",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onError = {
                                            Toast.makeText(
                                                context,
                                                it.localizedMessage,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                        )

                        IconButton(
                            onClick = {
                                controller.switchCamera()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cached,
                                contentDescription = "Flip Camera",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                    }

                }

            }

        }

    }
}