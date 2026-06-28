package com.pixless.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixless.app.camera.CameraScreen
import com.pixless.app.ui.screens.HomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {

            HomeScreen(

                onCameraClick = {
                    navController.navigate("camera")
                },

                onGalleryClick = {
                },

                onSettingsClick = {
                }

            )

        }

        composable("camera") {

            CameraScreen(
                onBack = {
                    navController.popBackStack()
                }
            )

        }

    }

}