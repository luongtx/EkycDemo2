package com.example.ekycdemo2.processor

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer

class TextProcessor : ImageAnalysis.Analyzer {

    var recognizer: TextRecognizer = TextRecognition.getClient()

    interface CallBackAnalyzer {
        fun onTextResults(texts: String)
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
                    processTextRecognitionResult(texts)
                    imageProxy.close()
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    imageProxy.close()
                }
        }
    }

    private fun processTextRecognitionResult(texts: Text) {
        callBackAnalyzer.onTextResults(texts.text)
    }

    fun close() {
        recognizer.close()
    }

}