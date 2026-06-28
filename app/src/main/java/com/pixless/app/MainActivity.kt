package com.pixless.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pixless.app.ui.screens.HomeScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContent {

            HomeScreen(

                onCameraClick = {},

                onGalleryClick = {},

                onSettingsClick = {}

            )
        }
    }
}