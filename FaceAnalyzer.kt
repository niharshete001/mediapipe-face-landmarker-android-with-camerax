package com.learn.mediapipefacedetection

import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class FaceAnalyzer(
    private val faceLandmarker: FaceLandmarker
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {

        try {

            val bitmap =
                imageProxy.toBitmap()

            val mpImage =
                BitmapImageBuilder(bitmap)
                    .build()

            faceLandmarker.detectAsync(
                mpImage,
                SystemClock.uptimeMillis()
            )

        } catch (e: Exception) {

            Log.e(
                "FaceAnalyzer",
                "Analysis Error",
                e
            )

        } finally {

            imageProxy.close()
        }
    }
}