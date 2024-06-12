package com.ingenieriajhr.testopencv

import android.content.Context
import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class OpenUtils(private val context: Context) {

    fun setUtil(bitmap: Bitmap): Bitmap {
        // Convert Bitmap to Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // Convert to grayscale
        //Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)

        // Load Haar Cascade
        val faceCascade = loadCascadeClassifier("haarcascade_frontalface_alt.xml")

        // Detect faces
        val faces = MatOfRect()
        faceCascade?.detectMultiScale(mat, faces)

        // Draw rectangles around detected faces
        for (rect in faces.toArray()) {

            Imgproc.rectangle(mat, rect.tl(), rect.br(), Scalar(255.0, 0.0, 0.0, 255.0), 2)
        }

        // Convert Mat back to Bitmap
        val resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, resultBitmap)

        return resultBitmap
    }

    private fun loadCascadeClassifier(filename: String): CascadeClassifier? {
        return try {
            // Load the cascade classifier from the raw resource
            val inputStream: InputStream = context.resources.openRawResource(
                context.resources.getIdentifier(filename.split(".")[0], "raw", context.packageName)
            )
            val cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE)
            val mCascadeFile = File(cascadeDir, filename)
            val outputStream = FileOutputStream(mCascadeFile)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            inputStream.close()
            outputStream.close()

            CascadeClassifier(mCascadeFile.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
