package com.learn.mediapipefacedetection

import android.util.Log
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

import kotlin.math.pow
import kotlin.math.sqrt

class ProctorEngine {

    private var closedEyeFrames = 0

    val history =
        ArrayDeque<AttentionState>()

    fun processResult(
        result: FaceLandmarkerResult
    ): AttentionState {


        if (result.faceLandmarks().isEmpty()) {
            return AttentionState.NO_FACE
        }

        val landmarks = result.faceLandmarks()[0]


        val eyeTop = landmarks[159]
        val eyeBottom = landmarks[145]
        val eyeLeft = landmarks[33]
        val eyeRight = landmarks[133]


        val ear =
            calculateEAR(
                eyeTop.x(),
                eyeTop.y(),
                eyeBottom.x(),
                eyeBottom.y(),
                eyeLeft.x(),
                eyeLeft.y(),
                eyeRight.x(),
                eyeRight.y()
            )

        Log.d("EAR", "ear=$ear")

        if (ear < 0.18) {

            closedEyeFrames++

        } else {

            closedEyeFrames = 0
        }

        if (closedEyeFrames > 10) {

            return AttentionState.EYES_CLOSED
        }


        // Face boundaries
        val leftFace = landmarks[234]
        val rightFace = landmarks[454]

        // Vertical references
        val forehead = landmarks[10]
        val chin = landmarks[152]

        // Nose
        val nose = landmarks[1]

        val centerX =
            (leftFace.x() + rightFace.x()) / 2f

        val centerY =
            (forehead.y() + chin.y()) / 2f

        val dx =
            nose.x() - centerX

        val dy =
            nose.y() - centerY

        Log.d(
            "HEAD_POSE",
            "dx=$dx dy=$dy"
        )

        // Working code for Head Movement
        val state = when {

            dx > 0.02f ->
                AttentionState.LOOKING_UP

            dy < -0.02f ->
                AttentionState.LOOKING_RIGHT

            dy > 0.02f ->
                AttentionState.LOOKING_LEFT

            dx < -0.03f ->
                AttentionState.LOOKING_DOWN


            else ->
                AttentionState.ATTENTIVE
        }

        return getStableState(state)
    }

    private fun getStableState(
        current: AttentionState
    ): AttentionState {

        history.addLast(current)

        if (history.size > 15) {
            history.removeFirst()
        }

        return history
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: current
    }


    private fun distance(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Double {

        return sqrt(
            (x1 - x2).toDouble().pow(2) +
                    (y1 - y2).toDouble().pow(2)
        )
    }


    private fun calculateEAR(
        topX: Float,
        topY: Float,
        bottomX: Float,
        bottomY: Float,
        leftX: Float,
        leftY: Float,
        rightX: Float,
        rightY: Float
    ): Double {

        val vertical =
            distance(
                topX,
                topY,
                bottomX,
                bottomY
            )

        val horizontal =
            distance(
                leftX,
                leftY,
                rightX,
                rightY
            )

        return vertical / horizontal
    }
}