package com.example.ekycdemo2

import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ekycdemo2.processor.IDCardProcessor
import com.example.ekycdemo2.processor.TTSSpeaker
import com.example.ekycdemo2.utils.Constants
import com.example.ekycdemo2.utils.Constants.Companion.REQUEST_CODE_PERMISSIONS
import com.example.ekycdemo2.utils.MediaFileIO
import kotlinx.android.synthetic.main.activity_text_recognition.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class IDCardScannerActivity : AppCompatActivity(), IDCardProcessor.CallBackAnalyzer {
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var preview: Preview
    private lateinit var cameraSelector: CameraSelector
    lateinit var cameraProvider: ProcessCameraProvider
    lateinit var idCardProcessor: IDCardProcessor
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

        ttsSpeaker = TTSSpeaker(this, Constants.speechText["front"]!!);
        idCardProcessor = IDCardProcessor(this)
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
            imageCapture = ImageCapture.Builder().setTargetResolution(Size(400, 300)).build()
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageAnalysis =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        idCardProcessor
                        idCardProcessor.setCallbacks(this)
                        it.setAnalyzer(cameraExecutor, idCardProcessor)
                    }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis)
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun rebindPreview() {
        stopPreview()
        tv_text_direct.text = getString(R.string.show_me_back)
        ttsSpeaker.speak(Constants.speechText["back"] ?: error(""))
        startCamera()
    }

    private fun stopPreview() {
        cameraProvider.unbindAll();
        Thread.sleep(1000)
    }

    private fun captureImage(isRebind: Boolean) {
        val imageCapture = imageCapture ?: return
        // Create time-stamped output file to hold the image
        val photoFile = MediaFileIO.createMediaFile(this)
        idCardProcessor.saveFilePath(photoFile.absolutePath);
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
//                    Toast.makeText(baseContext, "Photo capture succeeded ", Toast.LENGTH_LONG).show()
                    if (isRebind) rebindPreview()
                    else {
                        stopAll()
                    }
                }
            })
    }

    private fun stopAll() {
        cameraExecutor.shutdown()
        idCardProcessor.close()
        ttsSpeaker.speak(Constants.speechText["scanned_success"]!!);
        btn_next_step.visibility = View.VISIBLE
        btn_next_step.setOnClickListener { nextStep() }
    }

    private fun nextStep() {
//        tvStep.text = (getString(R.string.next_step_face_detection))
        val intent = Intent(this, FaceDetectionActivity::class.java)
        startActivity(intent)
    }

    override fun onTextResults(texts: String) {
        tv_result.text = texts;
    }

    override fun onCompleted() {
        captureImage(false)
    }

    override fun onRebindPreview() {
        captureImage(true)
    }
}