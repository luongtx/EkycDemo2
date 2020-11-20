package com.example.ekycdemo2.utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.ekycdemo2.FaceDetectionActivity
import com.example.ekycdemo2.IDCardScannerActivity.Companion.idCard
import com.example.ekycdemo2.model.OCRResults
import com.example.ekycdemo2.utils.Constants.Companion.API_ENDPOINT
import com.example.ekycdemo2.utils.Constants.Companion.AUTH_HEADER
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class OCRService : Service() {
    private var isRunning = false
    private lateinit var backgroundThread: Thread

    private val IDCardExtractor = Runnable {
        extractIDCard(idCard.storedFiles[0]);
    }

    override fun onCreate() {
        super.onCreate()
        this.isRunning = false;
        client = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.backgroundThread = Thread(IDCardExtractor);
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!this.isRunning) {
            isRunning = true;
            this.backgroundThread.start();
        }
        return START_NOT_STICKY;
    }


    private lateinit var client: OkHttpClient;

    private fun extractIDCard(file: File) {
        "image/jpeg".toMediaTypeOrNull()
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                File(file.absolutePath)
                    .asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
            .build()
        val request = Request.Builder()
            .url(API_ENDPOINT)
            .method("POST", body)
            .addHeader("Authorization", AUTH_HEADER)
            .build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                try {
                    var predictions = Gson().fromJson(
                        response.body?.string(),
                        OCRResults::class.java
                    ).result[0].predictions;
                    if (idCard.id == null) {
                        idCard.extract(predictions);
                        extractIDCard(idCard.storedFiles[1]);
                    } else {
                        idCard.extract(predictions);
                        stopSelf();
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Log.d(Constants.NETWORK, response.message);
            }
        }

    }

    private fun nextStep() {
        val intent = Intent(this, FaceDetectionActivity::class.java)
        startActivity(intent)
    }

}