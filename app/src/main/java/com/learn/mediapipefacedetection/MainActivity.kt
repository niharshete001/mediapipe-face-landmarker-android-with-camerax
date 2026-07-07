package com.learn.mediapipefacedetection

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var faceLandmarkerHelper: FaceLandmarkerHelper

    lateinit var previewView: PreviewView

    lateinit var hellotext: TextView

    private lateinit var proctorEngine: ProctorEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)
        hellotext = findViewById(R.id.hellotext)

        cameraExecutor = Executors.newSingleThreadExecutor()

        proctorEngine = ProctorEngine()

        startCamera()

        faceLandmarkerHelper = FaceLandmarkerHelper(
            this,
            { result ->
                runOnUiThread {

                    val state =
                        proctorEngine.processResult(result)

                    Log.d(
                        "PROCTOR",
                        state.name
                    )

                    hellotext.text = "User is ${state.name}"

                    processFace(result)
                }
            },
            { faceCount ->
                runOnUiThread {
                    if (faceCount == 1) {
                        //hellotext.text = "Hey..Nihar your face detected."
                    } else {
                        //hellotext.text = "Hey..Nihar your face hidden."
                    }
                    Log.d("FaceDetection", "Faces detected: $faceCount")
                }
            }
        )
    }

    private fun startCamera() {

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider =
                cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()

            preview.setSurfaceProvider(
                previewView?.surfaceProvider
            )


            val imageAnalyzer =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(
                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    )
                    .build()

            imageAnalyzer.setAnalyzer(
                cameraExecutor,
                FaceAnalyzer(
                    faceLandmarkerHelper.faceLandmarker
                )
            )

            val cameraSelector =
                CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )

        }, ContextCompat.getMainExecutor(this))
    }

    private fun processFace(
        result: FaceLandmarkerResult
    ) {

        if (result.faceLandmarks().isEmpty()) {

            Log.d("Proctor", "NO_FACE")

            return
        }

        val landmarks =
            result.faceLandmarks()[0]

        val nose =
            landmarks[1]

        Log.d(
            "Proctor",
            "Nose X=${nose.x()} Y=${nose.y()}"
        )
    }
}