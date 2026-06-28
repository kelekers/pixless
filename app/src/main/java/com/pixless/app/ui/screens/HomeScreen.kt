package com.pixless.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "PIXLESS",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "A Pixel Illustration Camera"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCameraClick
        ) {
            Text("Camera")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onGalleryClick
        ) {
            Text("Gallery")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onSettingsClick
        ) {
            Text("Settings")
        }
    }
}