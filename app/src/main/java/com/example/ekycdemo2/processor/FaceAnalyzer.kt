package com.example.ekycdemo2.processor

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.ekycdemo2.processor.util.FaceRotation
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay


class FaceAnalyzer : ImageAnalysis.Analyzer {

    private var faceDetector: FaceDetector

    init {
        //set firebase detector options
        val options = FaceDetectorOptions.Builder()
            .setClassificationMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .enableTracking()
            .build();
        faceDetector = FaceDetection.getClient(options);
    }

    interface CallBackAnalyzer {
        fun onFaceAngleChange(rotation: String)
    }

    private lateinit var callBackAnalyzer: CallBackAnalyzer

    fun setCallbacks(callBackAnalyzer: CallBackAnalyzer) {
        this.callBackAnalyzer = callBackAnalyzer
    }


    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to an ML Kit Vision API
            // ...
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    processListFace(faces)
                    imageProxy.close()
//                    Thread.sleep(100)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    imageProxy.close()
                }
        }
    }

    private fun processListFace(faces: List<Face>) {
        for (face in faces) {
            val rotY = face.headEulerAngleY
            val rotX = face.headEulerAngleX
            when {
                rotY > FaceRotation.ANGLE -> callBackAnalyzer.onFaceAngleChange(FaceRotation.LEFT)
                rotY < -FaceRotation.ANGLE -> callBackAnalyzer.onFaceAngleChange(FaceRotation.RIGHT)
                else -> {
                    when {
                        rotX > FaceRotation.ANGLE -> callBackAnalyzer.onFaceAngleChange(FaceRotation.UP)
                        rotX < -FaceRotation.ANGLE -> callBackAnalyzer.onFaceAngleChange(FaceRotation.DOWN)
                        rotX in -FaceRotation.STRAIGHT_BOUNDARY..FaceRotation.STRAIGHT_BOUNDARY &&
                                rotY in -FaceRotation.STRAIGHT_BOUNDARY..FaceRotation.STRAIGHT_BOUNDARY
                        -> callBackAnalyzer.onFaceAngleChange(FaceRotation.STRAIGHT)
                    }
                }
            }
        }
    }

    fun close() {
        faceDetector.close();
    }
}