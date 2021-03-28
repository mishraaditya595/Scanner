package com.vob.scanit.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
import android.media.ExifInterface
import android.os.Bundle
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vob.scanit.R
import kotlinx.android.synthetic.main.custom_camera_preview.*

/*CustomCameraPreview class opens the back camera and sets the camera preview surface*/
class CustomCameraPreview : AppCompatActivity() {

    private val TAG = "CustomCameraPreview"
    private var mOrientation = -1
    private var onClickOrientation = 1
    private var mCamera : Camera ? = null
    private var customCameraPreviewJava : CameraSurfaceView? = null
    private var cropX = 0.0
    private var cropY = 0.0
    private var cropWidth = 0.0
    private var cropHeight = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_camera_preview)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)

        val preview = mCamera?.parameters?.supportedPreviewSizes?.first()
        val pictureSize = mCamera?.parameters?.supportedPictureSizes!!.first()

        val cameraParameters = mCamera?.parameters
        cameraParameters?.focusMode = FOCUS_MODE_CONTINUOUS_VIDEO
        cameraParameters?.setPreviewSize(preview!!.width,preview.height)
        cameraParameters?.setPictureSize(pictureSize.width,pictureSize.height)
        mCamera?.parameters = cameraParameters


        customCameraPreviewJava = CameraSurfaceView(this,mCamera,0)
        camera_frame_layout.addView(customCameraPreviewJava)
        val kycType = intent.getStringExtra("KycType")
        document_type.text = "$kycType Capture"

        camera_preview_overlay.background = resources.getDrawable(R.drawable.full_aadhar_ovrlay)
        cropX = 0.2
        cropY = 0.1906
        cropWidth =0.6
        cropHeight = 0.6187

        /*when {
            kycType!!.toLowerCase().contains("full") -> {
                camera_preview_overlay.background = resources.getDrawable(R.drawable.full_aadhar_ovrlay)
                cropX = 0.2
                cropY = 0.1906
                cropWidth =0.6
                cropHeight = 0.6187
            }
            kycType.toLowerCase().contains("voter") -> {
                camera_preview_overlay.background = resources.getDrawable(R.drawable.voter_overlay)
                cropX = 0.25
                cropY = 0.1870
                cropWidth =0.5
                cropHeight = 0.4316
            }
            else -> {
                camera_preview_overlay.background = resources.getDrawable(R.drawable.pan_aaahar_overlay)
                cropX = 0.1
                cropY = 0.1667
                cropWidth =0.8
                cropHeight = 0.3174

            }
        }*/

        /*When the capture button gets clicked, the image is captured*/
        take_picture.setOnClickListener {
            onClickOrientation = mOrientation
            try {
                mCamera?.takePicture(null,null,pictureCallback)
            }catch (e : java.lang.Exception){
                Toast.makeText(this,"Error while taking picture!.Try Again.",Toast.LENGTH_SHORT).show()
            }
        }

        /*When user clicks on the close button, the preview is taken down*/
        close_camera_preview.setOnClickListener {
            setResult(RESULT_CANCELED,Intent())
            finish()
        }
    }

    /*The saveBitmap() function compresses the image to a standard size*/
    private fun saveBitmap(bitmap: Bitmap) : File{
        return try {
            val tempPath = cacheDir.absolutePath+"/TempImage.jpeg"
            val tempFile = File(tempPath)

            val fileOutputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream)
            fileOutputStream.close()
            tempFile

        }catch (e:java.lang.Exception){
            Log.d("","")
            File("")
        }
    }

    /*returns the image clicked, to the activity that called the camera*/
    private val pictureCallback = Camera.PictureCallback { data, camera
        ->
        try {
                var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

                val file = saveBitmap(bitmap)

                val exif = ExifInterface(file.toString())

                Log.d("TAG", "EXIF value >>" + exif.getAttribute(ExifInterface.TAG_ORIENTATION))
                when {
                    exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("6", ignoreCase = true
                    )
                    -> bitmap = rotate(bitmap, 90)
                    exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("8", ignoreCase = true
                    )
                    -> bitmap = rotate(bitmap, 270)
                    exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("3", ignoreCase = true
                    )
                    -> bitmap = rotate(bitmap, 180)
                    exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("0", ignoreCase = true
                    )
                    -> bitmap = rotate(bitmap, 90)
                }

            val width = bitmap.width.toFloat()
            val height = bitmap.height.toFloat()

            val croppedBitmap = Bitmap.createBitmap(bitmap,
                (width*cropX).toInt(),
                (height*cropY).toInt(),
                (width*cropWidth).toInt(),
                (height*cropHeight).toInt())


            val rotatedfile = saveBitmap(croppedBitmap)
            val returnIntent = Intent()
            returnIntent.putExtra("imagePath", rotatedfile.toURI().toString())
            setResult(RESULT_OK, returnIntent)
            finish()

            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    /*The rotate() function rotates the image by 90 degrees each time it is called */
    private fun rotate(bitmap: Bitmap, degrees: Int): Bitmap {
        return try {
            val matrix = Matrix()
            matrix.setRotate(degrees.toFloat())
            val oriented =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            oriented
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            bitmap
        }
    }

    /*Camera is released when activity gets paused, and later started again when we resume */
    override fun onPause() {
        super.onPause()
        mCamera?.release()
    }
}