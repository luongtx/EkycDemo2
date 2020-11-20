package com.example.ekycdemo2

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.ekycdemo2.model.IDCard
import com.example.ekycdemo2.processor.IDCardProcessor
import com.example.ekycdemo2.utils.Constants
import com.example.ekycdemo2.utils.MediaFileIO
import com.example.ekycdemo2.utils.OCRService
import kotlinx.android.synthetic.main.activity_text_recognition.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class IDCardScannerActivity : AppCompatActivity(), IDCardProcessor.CallBackAnalyzer {
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var preview: Preview
    private lateinit var cameraSelector: CameraSelector
    private var cameraProvider: ProcessCameraProvider? = null
    lateinit var idCardProcessor: IDCardProcessor
//    lateinit var ttsSpeaker: TTSSpeaker

    companion object {
        var idCard: IDCard = IDCard();
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognition)

//        ttsSpeaker = TTSSpeaker(this, Constants.speechText[IDCard.FRONT] ?: error(""));
        idCardProcessor = IDCardProcessor(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera(false);
        switch_auto.setOnCheckedChangeListener { _: CompoundButton, auto: Boolean ->
            var state = "";
            if (auto) {
                state = "bật";
                tv_text_direct.text = getString(R.string.show_me_front);
            } else {
                state = "tắt";
                tv_text_direct.text = getString(R.string.cap_front);
            }
            Toast.makeText(this, "Chế độ chụp tự động $state", Toast.LENGTH_SHORT).show();
            startCamera(auto);
        }

        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager;
        switch_light.setOnCheckedChangeListener { _: CompoundButton, switch: Boolean ->
            if (switch) {
                try {
                    val cameraId = cameraManager.cameraIdList[0];
                    cameraManager.setTorchMode(cameraId, true);
                } catch (e: CameraAccessException) {
                    Log.d(Constants.TAG, e.toString());
                }
            } else {
                try {
                    val cameraId = cameraManager.cameraIdList[0];
                    cameraManager.setTorchMode(cameraId, false);
                } catch (e: CameraAccessException) {
                    Log.d(Constants.TAG, e.toString());
                }
            }
        }

    }

    private fun startCamera(auto: Boolean) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(prv_text_recognition.createSurfaceProvider())
            }
            imageCapture = ImageCapture.Builder().setTargetResolution(Size(400, 300)).build()
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            if (auto) {
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
                    cameraProvider?.unbindAll()
                    cameraProvider?.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalysis
                    )
                    btnCap.visibility = View.GONE;
                } catch (e: Exception) {
                    Log.e(Constants.TAG, "Use case binding failed", e)
                }
            } else {
                try {
                    cameraProvider?.unbindAll()
                    cameraProvider?.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    btnCap.visibility = View.VISIBLE;
                    btnCap.setOnClickListener { captureImage(false) };
                } catch (e: Exception) {
                    Log.e(Constants.TAG, "Use case binding failed", e)
                }
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun rebindPreview(auto: Boolean) {
        Thread.sleep(1000);
        tv_text_direct.text =
            if (auto) getString(R.string.show_me_back) else getString(R.string.cap_back);
//        ttsSpeaker.speak(Constants.speechText[IDCard.BACK] ?: error(""))
        startCamera(auto);
    }

//    private fun stopPreview() {
//        cameraProvider.unbindAll();
//        Thread.sleep(1000)
//    }

    private fun captureImage(auto: Boolean) {
        val imageCapture = imageCapture ?: return
        // Create time-stamped output file to hold the image
        val photoFile = MediaFileIO.createMediaFile(this)
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(Constants.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(baseContext, "Photo capture succeeded ", Toast.LENGTH_LONG)
                        .show()
                    if (idCard.storedFiles.isEmpty()) {
                        idCard.storedFiles.add(photoFile)
                        rebindPreview(auto)
                    } else {
                        idCard.storedFiles.add(photoFile)
                        onPreProcessCompleted()
                    }
                }
            })
    }

    private fun onPreProcessCompleted() {
        cameraProvider?.unbindAll();
        cameraExecutor.shutdown()
        idCardProcessor.close()
        btn_next_step.visibility = View.VISIBLE
        startIDCardExtraction();
        btn_next_step.setOnClickListener { nextStep() }
    }

    private fun startIDCardExtraction() {
        val intent = Intent(this, OCRService::class.java);
        startService(intent);
    }

    private fun nextStep() {
        val intent = Intent(this, FaceDetectionActivity::class.java)
        startActivity(intent)
    }

    override fun onProcessed() {
        captureImage(true);
    }

}