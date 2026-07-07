package com.learn.mediapipefacedetection

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class FaceLandmarkerHelper(
    context: Context,
    private val onResult: (FaceLandmarkerResult) -> Unit,
    private val isFaceDetected: (Int) -> Unit
) {

    val faceLandmarker: FaceLandmarker

    init {

        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("face_landmarker.task")
            .build()

        val options = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumFaces(1)
            .setOutputFaceBlendshapes(true)
            .setOutputFacialTransformationMatrixes(true)
            .setResultListener { result, _ ->

                onResult(result)

                if (result.faceLandmarks().isNotEmpty()) {

                    val landmarks =
                        result.faceLandmarks()[0]

                    Log.d(
                        "LANDMARKS",
                        "Total = ${landmarks.size}"
                    )
                }

                isFaceDetected(result.faceLandmarks().size)

                Log.d(
                    "FACE_RESULT",
                    "Faces Detected = ${result.faceLandmarks().size}"
                )


            }
            .setErrorListener { error ->

                Log.e("MediaPipe", error.message ?: "Unknown Error")

            }
            .build()

        faceLandmarker =
            FaceLandmarker.createFromOptions(
                context,
                options
            )
    }
}