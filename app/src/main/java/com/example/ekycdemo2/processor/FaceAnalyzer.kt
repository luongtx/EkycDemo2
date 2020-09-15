package com.example.ekycdemo2.processor

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.ekycdemo2.processor.util.FaceRotation
import com.google.firebase.ml.vision.common.FirebaseVisionImage

import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector

class FaceAnalyzer(val faceDetector: FirebaseVisionFaceDetector) : ImageAnalysis.Analyzer {


    interface CallBackAnalyzer {
        fun onFaceAngleChange(rotation: Int)
    }

    private lateinit var callBackAnalyzer: CallBackAnalyzer

    fun setCallbacks(callBackAnalyzer: CallBackAnalyzer) {
        this.callBackAnalyzer = callBackAnalyzer
    }

    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val imageRotation = degreesToFirebaseRotation(imageProxy.imageInfo.rotationDegrees)
        if (mediaImage != null) {
            val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
            // Pass image to an ML Kit Vision API
            // ...
            faceDetector.detectInImage(image)
                .addOnSuccessListener {
                    faces -> processListFace(faces)
                }
                .addOnFailureListener {

                }
        }
    }
    private fun processListFace(faces: List<FirebaseVisionFace>) {
        for (face in faces) {
            var rotY = face.headEulerAngleY
//            println("rotY: $rotY")
//            println("rotX: $rotX")
            when {
                rotY > FaceRotation.ANGLE -> callBackAnalyzer.onFaceAngleChange(FaceRotation.LEFT)
                rotY < -FaceRotation.ANGLE -> callBackAnalyzer.onFaceAngleChange(FaceRotation.RIGHT)
                rotY in -FaceRotation.STRAIGHT_BOUNDARY..FaceRotation.STRAIGHT_BOUNDARY
                -> callBackAnalyzer.onFaceAngleChange(FaceRotation.STRAIGHT)
            }
        }
    }
}