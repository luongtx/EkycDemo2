package com.example.ekycdemo3

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.ekycdemo3.model.FaceVerification
import com.example.ekycdemo3.processor.FaceAnalyzer
import com.example.ekycdemo3.processor.util.FaceRotation
import com.example.ekycdemo3.processor.util.FaceRotation.Companion.targetFaceRotations
import com.example.ekycdemo3.utils.Constants
import com.example.ekycdemo3.utils.MediaFileIO
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_face_detection.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@RequiresApi(Build.VERSION_CODES.O)
class FaceDetectionActivity : AppCompatActivity(), FaceAnalyzer.CallBackAnalyzer {
    lateinit var cameraExecutor: ExecutorService
    lateinit var faceAnalyzer: FaceAnalyzer

    //    lateinit var ttsSpeaker: TTSSpeaker
    private var imageCapture: ImageCapture? = null

    lateinit var progressDialog: ProgressDialog;

    //lateinit var tfv_direct: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)

        startCamera();

        val direct = FaceRotation.directionOf[targetFaceRotations.first()];
        tv_direct.text = direct
        faceAnalyzer = FaceAnalyzer()
//        ttsSpeaker =
//            TTSSpeaker(this, Constants.speechText[targetFaceRotations.first()] ?: error(""))
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
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(Constants.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                    saveFilePath(photoFile.absolutePath)
                    savePrefsData("face_path", photoFile.absolutePath);
                    Log.d(Constants.TAG, "Photo was captured!")
                }
            })
    }

//    private fun saveFilePath(path: String) {
//        val sharedReferenced = getSharedPreferences("prefs", Context.MODE_PRIVATE)
//        val editor = sharedReferenced.edit()
//        editor.putString("face_path", path)
//        editor.apply()
//    }

    private fun savePrefsData(key: String, value: String) {
        val sharedReferenced = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = sharedReferenced.edit()
        editor.putString(key, value)
        editor.apply()
    }

    @JvmName("getOutputDirectory1")

    private fun stopDetection() {
        tv_direct.text = "";
        Thread.sleep(1000)
        faceAnalyzer.close()
        cameraExecutor.shutdown()
    }

    private fun nextStep() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    private fun onDetectionCompleted() {
        stopDetection()
        startVerification()
    }

    private fun startVerification() {
        //create encode base64 for id card image
        IDCardScannerActivity.idCard.storedFiles[0].readBytes();
        val byteArrayIDCard =
            Files.readAllBytes(Paths.get(IDCardScannerActivity.idCard.storedFiles[0].absolutePath));
        val encodedIDCardString = java.util.Base64.getEncoder().encodeToString(byteArrayIDCard);

        //create encode base64 for face image
        val sharedReferenced = getSharedPreferences("prefs", MODE_PRIVATE)
        val pathFace = sharedReferenced.getString("face_path", "")
        val byteArrayFace = Files.readAllBytes(Paths.get(pathFace));
        val encodedFaceString = java.util.Base64.getEncoder().encodeToString(byteArrayFace);

        // call api client
        val client = OkHttpClient().newBuilder()
            .build();
        val mediaType = "application/json".toMediaTypeOrNull();

        val jsonObject = JSONObject();
        jsonObject.put("model_name", "VGG-Face");
        val jsonArray = JSONArray();
        val jsonChild = JSONObject();
        jsonChild.put("img1", "data:image/jpeg;base64,$encodedIDCardString");
        jsonChild.put("img2", "data:image/jpeg;base64,$encodedFaceString");
        jsonArray.put(jsonChild);
        jsonObject.put("img", jsonArray);

        val requestBody = jsonObject.toString().toRequestBody(mediaType);

        val request: Request = Request.Builder()
            .url("http://192.168.43.8:5000/verify")
            .method("POST", requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                println("An error occur: "+e.printStackTrace());
                Log.d("ERROR: ", e.message!!);
                progressDialog.dismiss();
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        progressDialog.dismiss();
                        val faceVerification =
                            Gson().fromJson(response.body!!.string(), FaceVerification::class.java);
                        Log.i("RESULT BODY: ", faceVerification.pair.toString());
                        if (faceVerification.pair.verified) {
                            iv_check.visibility = View.VISIBLE
                            tv_direct.text = (getString(R.string.auth_success))
                            val precision = (1 - faceVerification.pair.distance) * 100;
                            savePrefsData("precision", precision.toString());
                            nextStep();
                        } else {
                            iv_check.setImageResource(R.drawable.error_center_x);
                            tv_direct.text = getString(R.string.auth_failed);
                        }
                    }
                }
            }
        })
        showProgressDialog()
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Vui lòng đợi...")
        progressDialog.show()
    }

    override fun onFaceAngleChange(rotation: String) {
        tv_rotation.text = (getString(R.string.head_is) + rotation)
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
//        ttsSpeaker.speak(Constants.speechText[targetFaceRotations.first()] ?: error(""))
    }
}

