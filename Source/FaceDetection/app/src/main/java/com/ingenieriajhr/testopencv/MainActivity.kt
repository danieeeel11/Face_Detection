package com.ingenieriajhr.testopencv

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageProxy
import com.ingenieriajhr.testopencv.databinding.ActivityMainBinding
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr
import com.ingenieriiajhr.jhrCameraX.ImageProxyResponse
import org.opencv.android.OpenCVLoader
import androidx.activity.result.contract.ActivityResultContracts.*

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var cameraJhr: CameraJhr
    private lateinit var openUtils: OpenUtils
    //private lateinit var btnCamera: Button

    companion object {
        private const val CAMERA_FRONT = 1
        private const val CAMERA_BACK = 0
    }

    private var currentCamera = CAMERA_BACK
    private var isCameraStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openUtils = OpenUtils(this)

        if (OpenCVLoader.initDebug()) {
            Log.d("OPENCV2023", "TRUE")
        } else {
            Log.d("OPENCV2023", "INCORRECTO")
        }

        // Init cameraJHR
        cameraJhr = CameraJhr(this)

        /*btnCamera = findViewById(R.id.btnCamera)
        btnCamera.setOnClickListener {
            openCamera()
        }*/
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && cameraJhr.allpermissionsGranted() && !isCameraStarted) {
            startCameraJhr()
            isCameraStarted = true
        } else {
            cameraJhr.noPermissions()
        }
    }

    /*fun openCamera() {
        // Switch between front and back cameras
        currentCamera = if (currentCamera == CAMERA_BACK) CAMERA_FRONT else CAMERA_BACK
        // Restart the camera with the new camera
        //startCameraJhr()

    }*/

    private fun startCameraJhr() {
        cameraJhr.addlistenerBitmap(object : BitmapResponse {
            override fun bitmapReturn(bitmap: Bitmap?) {
                val newBitmap = openUtils.setUtil(bitmap!!)
                if (bitmap != null) {
                    runOnUiThread {
                        binding.imgBitMap.setImageBitmap(newBitmap)
                    }
                }
            }
        })

        cameraJhr.initBitmap()



        // Selector camera LENS_FACING_FRONT = 0; LENS_FACING_BACK = 1;
        // Aspect Ratio  RATIO_4_3 = 0; RATIO_16_9 = 1; false returnImageProxy, true return bitmap
        cameraJhr.start(currentCamera, 0, binding.cameraPreview, true, false, true)
    }

}
