package com.pixless.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixless.app.camera.CameraScreen
import com.pixless.app.gallery.GalleryScreen

object Routes {
    const val CAMERA = "camera"
    const val GALLERY = "gallery"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.CAMERA
    ) {

        composable(Routes.CAMERA) {
            CameraScreen(
                onNavigateToGallery = {
                    navController.navigate(Routes.GALLERY)
                }
            )
        }

        composable(Routes.GALLERY) {
            GalleryScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}