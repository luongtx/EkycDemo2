package com.example.ekycdemo2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.ekycdemo2.processor.FaceAnalyzer
import com.example.ekycdemo2.processor.TTSSpeaker
import com.example.ekycdemo2.processor.util.FaceRotation
import com.example.ekycdemo2.processor.util.FaceRotation.Companion.targetFaceRotations
import com.example.ekycdemo2.utils.Constants
import com.example.ekycdemo2.utils.MediaFileIO
import kotlinx.android.synthetic.main.activity_face_detection.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class FaceDetectionActivity : AppCompatActivity(), FaceAnalyzer.CallBackAnalyzer {
    lateinit var cameraExecutor: ExecutorService
    lateinit var faceAnalyzer: FaceAnalyzer
    lateinit var ttsSpeaker: TTSSpeaker
    private var imageCapture: ImageCapture? = null

    //lateinit var tfv_direct: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)

        startCamera();

        val direct = FaceRotation.directionOf[targetFaceRotations.first()];
        tv_direct.text = direct
        faceAnalyzer = FaceAnalyzer()
        ttsSpeaker = TTSSpeaker(this, Constants.speechText[targetFaceRotations.first()] ?: error(""))
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(prv_face_detection.createSurfaceProvider()) }

            imageCapture = ImageCapture.Builder().setTargetResolution(Size(400, 300)).build()
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(360, 480))
                .build()
                .also {
                    faceAnalyzer
                    faceAnalyzer.setCallbacks(this)
                    it.setAnalyzer(cameraExecutor, faceAnalyzer)
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))

    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return
        // Create time-stamped output file to hold the image
        val photoFile = MediaFileIO.createMediaFile(this)
        saveFilePath(photoFile.absolutePath)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(Constants.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d(Constants.TAG, "Photo was captured!")
                }
            })
    }

    private fun saveFilePath(path: String) {
        val sharedPreferenced = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferenced.edit()
        editor.putString("img_face", path)
        editor.apply()
    }

    @JvmName("getOutputDirectory1")

    private fun stopAll() {
        Thread.sleep(1000)
        faceAnalyzer.close()
        cameraExecutor.shutdown()
    }

    private fun nextStep() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    private fun onDetectionCompleted() {
        iv_check.visibility = View.VISIBLE
        tv_direct.text = (getString(R.string.auth_success))
        stopAll()
        nextStep()
    }

    override fun onFaceAngleChange(rotation: String) {
        tv_rotation.text = rotation
        if (rotation == targetFaceRotations[0]) {
            if (rotation == FaceRotation.STRAIGHT) captureImage()
            targetFaceRotations.removeAt(0)
            if (targetFaceRotations.isEmpty()) {
                onDetectionCompleted()
                return
            }
            nextDirection()
        }
    }

    private fun nextDirection() {
        tv_direct.text = FaceRotation.directionOf[targetFaceRotations.first()]
        ttsSpeaker.speak(Constants.speechText[targetFaceRotations.first()] ?: error(""))
    }
}

