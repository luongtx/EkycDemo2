package com.example.ekycdemo2.processor.util

class FaceRotation {
    companion object {
        const val STRAIGHT = "straight"
        const val LEFT = "left"
        const val RIGHT = "right"
        const val UP = "up"
        const val DOWN = "down"
        val directionOf = mapOf(
            STRAIGHT to "Nhìn thẳng vào camera",
            LEFT to "Quay mặt sang trái",
            UP to "Quay mặt lên trên",
            DOWN to "Quay mặt xuống dưới",
            RIGHT to "Quay mặt sang phải"
        )
        const val ANGLE = 40
        const val STRAIGHT_BOUNDARY = 5.0
        val targetFaceRotations = ArrayList(listOf(LEFT, RIGHT, DOWN, STRAIGHT, UP).shuffled())
    }
}