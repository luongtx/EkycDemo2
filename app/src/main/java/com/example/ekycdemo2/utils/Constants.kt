package com.example.ekycdemo2.utils

import com.example.ekycdemo2.model.IDCard
import com.example.ekycdemo2.processor.util.FaceRotation

class Constants {
    companion object {
        const val TAG = "CameraXBasic"
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA);
        val speechText = mapOf(
//            "scanned_success" to " Your ID Card has been successfully scanned, please move to next step",
            IDCard.FRONT to "Please show me the front of your identity card",
            IDCard.BACK to "Please show me the back of your identity card",
            FaceRotation.UP to "Please move your face up",
            FaceRotation.DOWN to "Please move your face down",
            FaceRotation.STRAIGHT to "Please keep your face straight",
            FaceRotation.LEFT to "Please move your face to the left",
            FaceRotation.RIGHT to "Please move your face to the right"
        )
        const val ROOT_NODE = "id_cards"
    }
}