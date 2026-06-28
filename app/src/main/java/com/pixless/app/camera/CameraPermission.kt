package com.pixless.app.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*

import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestCameraPermission(
    content: @Composable () -> Unit
) {

    val context = LocalContext.current

    var granted by remember {

        mutableStateOf(

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        )

    }

    val launcher = rememberLauncherForActivityResult(

        ActivityResultContracts.RequestPermission()

    ) {

        granted = it

    }

    LaunchedEffect(Unit) {

        if (!granted) {

            launcher.launch(Manifest.permission.CAMERA)

        }

    }

    if (granted) {

        content()

    }

}