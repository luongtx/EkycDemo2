package com.example.ekycdemo2.utils

class Constants {
    companion object {
        const val TAG = "CameraXBasic"
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA);
        val speechText = mapOf(
            "front" to "Please show me the front of your IDCard",
            "back" to "Please show me the back of your IDCard",
            "up" to "Please move your face up",
            "down" to "Please move your face down",
            "straight" to "Please keep your face straight",
            "left" to "Please move your face to the left",
            "right" to "Please move your face to the right"
        )
    }
}