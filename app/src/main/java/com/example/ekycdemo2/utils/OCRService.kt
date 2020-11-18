package com.example.ekycdemo2.utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.ekycdemo2.IDCardScannerActivity.Companion.idCard
import com.example.ekycdemo2.model.OCRResults
import com.example.ekycdemo2.utils.Constants.Companion.API_ENDPOINT
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException


class OCRService : Service() {
    private var isRunning = false
    private lateinit var backgroundThread: Thread

    private val IDCardExtractor = Runnable {
        extractIDCard(idCard.storedFiles[0]);
    }

    override fun onCreate() {
        super.onCreate()
        this.isRunning = false;
        client = OkHttpClient().newBuilder().build();
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
        "text/plain".toMediaTypeOrNull()
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                RequestBody.create(
                    "application/octet-stream".toMediaTypeOrNull(),
                    File(file.absolutePath)
                )
            )
            .build()
        val request = Request.Builder()
            .url(API_ENDPOINT)
            .method("POST", body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(Constants.TAG, e.toString());
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    var result = Gson().fromJson(
                        response.body?.string(),
                        OCRResults::class.java
                    ).idCards?.get(0);
                    idCard.extract(result!!);
                    if (result.id != null) {
                        extractIDCard(idCard.storedFiles[1]);
                    } else {
                        stopSelf();
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

}