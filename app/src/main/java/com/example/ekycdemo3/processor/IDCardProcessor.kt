package com.example.ekycdemo3.processor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.ekycdemo3.IDCardScannerActivity.Companion.idCard
import com.example.ekycdemo3.utils.Constants.Companion.TAG
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer

class IDCardProcessor(val context: Context) : ImageAnalysis.Analyzer {

    var recognizer: TextRecognizer = TextRecognition.getClient()

    interface CallBackAnalyzer {
        fun onProcessed()
    }

    private lateinit var callBackAnalyzer: CallBackAnalyzer

    fun setCallbacks(callBackAnalyzer: CallBackAnalyzer) {
        this.callBackAnalyzer = callBackAnalyzer
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image;
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { texts ->
                    processResults(texts)
                    imageProxy.close()
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    imageProxy.close()
                }
        }
    }

    private fun processResults(texts: Text) {
//        Log.d(TAG, texts.text)
        val blocks = texts.textBlocks
        if (blocks.size > 0) {
            val lines = blocks.flatMap { block -> block.lines }
            process(lines)
        }
    }

    private fun process(lines: List<Text.Line>) {
        if (idCard.storedFiles.isEmpty()) {
            var check1 = false;
            var check2 = false;
            for (line in lines) {
                if (line.text.contains(Regex("\\d{9}"))) {
                    check1 = true;
                }
                if (line.text.contains("DKHK")) {
                    check2 = true;
                }
            }
            if (check1 && check2) {
                try {
                    Log.d(TAG, "ID card detected");
                    callBackAnalyzer.onProcessed()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
            }
        } else {
            for (line in lines) {
                if (line.text.contains("DAU VET")) {
                    try {
                        callBackAnalyzer.onProcessed()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return
                    }
                }
            }
        }
    }

    fun close() {
        recognizer.close()
    }

}