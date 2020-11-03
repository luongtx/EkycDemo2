package com.example.ekycdemo2.processor.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.ekycdemo2.IDCardScannerActivity
import com.example.ekycdemo2.model.IDCard
import com.example.ekycdemo2.model.OCRResults
import com.example.ekycdemo2.repos.IDCardRepo
import com.example.ekycdemo2.repos.impl.IDCardRepoImpl
import com.example.ekycdemo2.utils.Constants
import com.google.gson.Gson
import okhttp3.*
import java.io.File
import java.io.IOException


class OCRService : Service() {
    private var isRunning = false
    private lateinit var backgroundThread: Thread

    private lateinit var idCard: IDCard;
    private lateinit var idCardRepo: IDCardRepo;

    private val IDCardExtraction = Runnable {
        idCard = IDCardScannerActivity.idCard;
        idCardRepo = IDCardRepoImpl();
        extractIDCard(idCard.storeFiles[0]);
        extractIDCard(idCard.storeFiles[1]);
    }

    override fun onCreate() {
        super.onCreate()
        this.isRunning = false;
        client = OkHttpClient().newBuilder().build();
        this.backgroundThread = Thread(IDCardExtraction);
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!this.isRunning) {
            isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }


    private lateinit var client: OkHttpClient;

    private fun extractIDCard(file: File) {
        MediaType.parse("text/plain")
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", file.name,
                RequestBody.create(
                    MediaType.parse("application/octet-stream"),
                    File(file.absolutePath)
                )
            )
            .build()
        val request = Request.Builder()
            .url("http://203.171.20.92:5001/api/id-extraction")
            .method("POST", body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(Constants.TAG, e.toString());
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val result = Gson().fromJson(response.body()?.string(), OCRResults::class.java);
                    result.idCards?.get(0)?.let { idCard.extract(it) };
                    idCardRepo.saveIDCard(idCard);
                    if (idCard.issueDate != null) stopSelf();
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })
    }

}