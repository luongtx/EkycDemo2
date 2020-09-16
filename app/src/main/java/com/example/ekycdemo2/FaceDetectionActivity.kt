package com.example.ekycdemo2

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ekycdemo2.processor.FaceAnalyzer
import com.example.ekycdemo2.processor.TTSSpeaker
import com.example.ekycdemo2.processor.util.FaceRotation
import com.example.ekycdemo2.processor.util.FaceRotation.Companion.targetFaceRotations
import com.example.ekycdemo2.utils.Constants
import com.example.ekycdemo2.utils.Constants.Companion.REQUEST_CODE_PERMISSIONS
import com.example.ekycdemo2.utils.Constants.Companion.REQUIRED_PERMISSIONS
import kotlinx.android.synthetic.main.activity_face_detection.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceDetectionActivity : AppCompatActivity(), FaceAnalyzer.CallBackAnalyzer {
    lateinit var cameraExecutor: ExecutorService
    lateinit var faceAnalyzer: FaceAnalyzer
    lateinit var ttsSpeaker: TTSSpeaker

    //lateinit var tfv_direct: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)

        //request permission
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        tv_direct.text = (getString(R.string.turn_your_face) + FaceRotation.valueOfs[targetFaceRotations.first()])
        faceAnalyzer = FaceAnalyzer()
        ttsSpeaker = TTSSpeaker(this, tv_direct.text.toString())
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        // bind the lifecycle of cameras to the lifecycle owner.
        // This eliminates the task of opening and closing the camera since CameraX is lifecycle-aware.
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(prv_face_detection.createSurfaceProvider()) }

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
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))

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


    private fun onDetectionCompleted() {
        faceAnalyzer.close()
        cameraExecutor.shutdown()
        Thread.sleep(1000)
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }


    override fun onFaceAngleChange(rotation: Int) {
        tv_rotation.text = (getString(R.string.head_is) + FaceRotation.valueOfs[rotation])
        if (rotation == targetFaceRotations.first()) {
            targetFaceRotations.remove(targetFaceRotations.first())
            if (targetFaceRotations.isEmpty()) {
                tv_direct.text = (getString(R.string.auth_success))
                onDetectionCompleted()
                return
            }
            tv_direct.text = if (targetFaceRotations.first() == FaceRotation.STRAIGHT) getString(R.string.keep_straight)
            else "${R.string.turn_your_face} ${FaceRotation.valueOfs[targetFaceRotations.first()]}"
            ttsSpeaker.speak(tv_direct.text.toString())
        }
    }
}

