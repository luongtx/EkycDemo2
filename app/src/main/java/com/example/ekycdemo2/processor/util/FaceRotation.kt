package com.example.ekycdemo2.processor.util

class FaceRotation {
    companion object {
        const val STRAIGHT = 0
        const val LEFT = 1
        const val RIGHT = 2
        val valueOfs = mapOf(STRAIGHT to "straight", LEFT to "left", RIGHT to "right")
        const val ANGLE = 40
        const val STRAIGHT_BOUNDARY = 10.0
    }
}
