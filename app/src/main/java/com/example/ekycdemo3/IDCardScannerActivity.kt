package com.example.ekycdemo3

import android.app.ProgressDialog
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
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.ekycdemo3.model.IDCard
import com.example.ekycdemo3.model.OCRResults
import com.example.ekycdemo3.processor.IDCardProcessor
import com.example.ekycdemo3.utils.Constants
import com.example.ekycdemo3.utils.Constants.Companion.I_HEIGHT
import com.example.ekycdemo3.utils.Constants.Companion.I_WIDTH
import com.example.ekycdemo3.utils.MediaFileIO
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_text_recognition.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class IDCardScannerActivity : AppCompatActivity(), IDCardProcessor.CallBackAnalyzer {
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var preview: Preview
    private lateinit var cameraSelector: CameraSelector
    private var cameraProvider: ProcessCameraProvider? = null
    lateinit var idCardProcessor: IDCardProcessor
    lateinit var progressDialog: ProgressDialog;
//    lateinit var ttsSpeaker: TTSSpeaker


    private lateinit var httpClient: OkHttpClient;

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

        httpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    }

    private fun startCamera(auto: Boolean) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(prv_text_recognition.createSurfaceProvider())
            }
            imageCapture = ImageCapture.Builder().setTargetResolution(Size(I_WIDTH, I_HEIGHT)).build()
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
                    cameraProvider?.unbindAll();
                    idCard.storedFiles.add(photoFile);
//                    onProcessCompleted();
                    extractIDCard(photoFile, auto);
                }
            })
    }

    private fun extractIDCard(file: File, auto: Boolean) {
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                File(file.absolutePath)
                    .asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
            .build()
        val request = Request.Builder()
            .url(Constants.API_ENDPOINT)
            .method("POST", body)
            .addHeader("Authorization", Constants.AUTH_HEADER)
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss();

                Log.d(Constants.NETWORK, e.message.toString());
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        try {
                            val predictions = Gson().fromJson(
                                response.body?.string(),
                                OCRResults::class.java
                            ).result[0].predictions;
                            progressDialog.dismiss();
                            idCard.extract(predictions);
                            if (idCard.isFilled) onProcessCompleted();
                            else rebindPreview(auto);
                        } catch (e: Exception) {
                            idCard.onExtractError();
                            showErrorDialog();
                            startCamera(auto);
                        }
                    }
                } else {
                    Log.d(Constants.NETWORK, response.message);
                }
            }
        })
        showProgressDialog();
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Đang xử lý...")
        progressDialog.show()
    }

    private fun showErrorDialog() {
        SweetAlertDialog(
            this@IDCardScannerActivity, SweetAlertDialog.ERROR_TYPE
        )
            .setTitleText("Oops...")
            .setContentText("Đã xảy ra lỗi, vui lòng thử lại!")
            .show()
    }

    private fun onProcessCompleted() {
        cameraExecutor.shutdown()
        val intent = Intent(this, FaceDetectionActivity::class.java)
        startActivity(intent)
    }

    override fun onProcessed() {
        captureImage(true);
    }

}