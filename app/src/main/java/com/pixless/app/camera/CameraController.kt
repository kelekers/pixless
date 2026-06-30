package com.pixless.app.camera

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import kotlinx.coroutines.delay
import com.pixless.app.imageprocessing.PixlessProcessor
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.MeteringPointFactory

class CameraController(
    private val context: Context
) {

    // ENUM DECLARATION
    enum class FlashMode {
        OFF, ON, TORCH
    }

    // STATE VARIABLES
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    private val imageCapture = ImageCapture.Builder()
        .setTargetAspectRatio(androidx.camera.core.AspectRatio.RATIO_4_3)
        .build()
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashMode = FlashMode.OFF
    private var camera: Camera? = null

    private lateinit var previewView: PreviewView
    private lateinit var lifecycleOwner: LifecycleOwner

    // CAMERA INITIALIZATION
    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        this.previewView = previewView
        this.lifecycleOwner = lifecycleOwner

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(androidx.camera.core.AspectRatio.RATIO_4_3)
                .build().apply {
                surfaceProvider = previewView.surfaceProvider
            }

            imageCapture.flashMode = when (flashMode) {
                FlashMode.OFF, FlashMode.TORCH -> ImageCapture.FLASH_MODE_OFF
                FlashMode.ON -> ImageCapture.FLASH_MODE_ON
            }

            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            camera?.cameraControl?.enableTorch(flashMode == FlashMode.TORCH)
        }, ContextCompat.getMainExecutor(context))
    }

    fun startCameraWithPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        onFrameProcessed: (Bitmap) -> Unit
    ) {
        this.previewView = previewView
        this.lifecycleOwner = lifecycleOwner

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply { surfaceProvider = previewView.surfaceProvider }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(android.util.Size(480, 640))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), PixlessAnalyzer(onFrameProcessed))

            cameraProvider.unbindAll()
            // PASTIKAN imageCapture DAN imageAnalysis KEDUANYA DI-BIND
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalysis
            )
        }, ContextCompat.getMainExecutor(context))
    }

    // FLASH MANAGEMENT
    fun setFlashMode(mode: FlashMode) {
        flashMode = mode
        imageCapture.flashMode = when (mode) {
            FlashMode.OFF, FlashMode.TORCH -> ImageCapture.FLASH_MODE_OFF
            FlashMode.ON -> ImageCapture.FLASH_MODE_ON
        }
        camera?.cameraControl?.enableTorch(mode == FlashMode.TORCH)
    }

    fun getFlashMode(): FlashMode = flashMode

    // CAPTURE ACTION
    fun takePhoto(
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val name = "Pixless_${System.currentTimeMillis()}"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Pixless")
            }
        }

        val outputOptions = OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onSuccess()
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }

    fun takeAndProcessPhoto(
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
        scope: CoroutineScope
    ) {
        takePhoto(
            onSuccess = {
                scope.launch(Dispatchers.IO) {
                    delay(500)

                    val uri = getLastPhotoUri()
                    if (uri != null) {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            val bitmap = BitmapFactory.decodeStream(inputStream)

                            val processor = PixlessProcessor()
                            val processedBitmap = processor.processImage(bitmap)

                            saveBitmapToMediaStore(processedBitmap)
                        }
                        withContext(Dispatchers.Main) { onSuccess() }
                    }
                }
            },
            onError = onError
        )
    }

    fun setFocus(meteringPointFactory: MeteringPointFactory, x: Float, y: Float) {
        val point = meteringPointFactory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point).build()
        camera?.cameraControl?.startFocusAndMetering(action)
    }

    // CAMERA SWITCHING
    fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        if (::previewView.isInitialized && ::lifecycleOwner.isInitialized) {
            startCamera(previewView, lifecycleOwner)
        }
    }

    // MEDIA READER
    fun getPixlessPhotos(): List<Uri> {
        val uris = mutableListOf<Uri>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)

        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("Pixless_%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(collection, id)
                uris.add(contentUri)
            }
        }
        return uris
    }

    private fun saveBitmapToMediaStore(bitmap: Bitmap): Uri? {
        val name = "Pixless_Retro_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Pixless")
            }
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        uri?.let {
            val outputStream = context.contentResolver.openOutputStream(it)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            }
        }
        return uri
    }

    fun getLastPhotoUri(): Uri? {
        return getPixlessPhotos().firstOrNull()
    }
}