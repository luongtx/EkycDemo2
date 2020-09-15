package com.example.ekycdemo2

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ekycdemo2.processor.TextProcessor
import com.example.ekycdemo2.processor.TTSSpeaker
import com.example.ekycdemo2.utils.Constants
import com.example.ekycdemo2.utils.Constants.Companion.FILENAME_FORMAT
import com.example.ekycdemo2.utils.Constants.Companion.REQUEST_CODE_PERMISSIONS
import kotlinx.android.synthetic.main.activity_text_recognition.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class TextRecognitionActivity : AppCompatActivity(), TextProcessor.CallBackAnalyzer {
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var preview: Preview
    private lateinit var cameraSelector: CameraSelector
    private lateinit var outputDirectory: File
    lateinit var cameraProvider: ProcessCameraProvider
    lateinit var textProcessor: TextProcessor
    lateinit var ttsSpeaker: TTSSpeaker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognition)
        //request permission
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        outputDirectory = getOutputDirectory()

        btnCapture.setOnClickListener {
            onClickCaptureImage()
        }
        ttsSpeaker = TTSSpeaker(this, tv_text_direct.text.toString())
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun allPermissionsGranted() = Constants.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(prv_text_recognition.createSurfaceProvider())
            }
            imageCapture = ImageCapture.Builder().build()
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            textProcessor = TextProcessor()
            val imageAnalysis =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        textProcessor
                        textProcessor.setCallbacks(this)
                        it.setAnalyzer(cameraExecutor, textProcessor)
                    }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis)
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    private fun rebindPreview() {
        tv_text_direct.text = ("Please show your ID card back")
        ttsSpeaker.speak(tv_text_direct.text.toString())
        startCamera()
    }

    private fun stopPreview() {
        cameraProvider.unbindAll();
        Thread.sleep(1000)
    }

    var times: Int = 1
    private fun onClickCaptureImage() {
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()


        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(Constants.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(Constants.TAG, msg)
                    stopPreview()
                    if (times++ == 2) {
                        onRecognitionCompleted()
                        return
                    }
                    rebindPreview()
                }
            })
    }

    private fun onRecognitionCompleted() {
        cameraExecutor.shutdown()
        textProcessor.close()
        val intent = Intent(this, FaceDetectionActivity::class.java)
        startActivity(intent)
    }

    override fun onTextResults(texts: String) {
        tv_result.text = texts;
    }

}