package com.vob.scanit.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.theartofdev.edmodo.cropper.CropImage
import com.vob.scanit.R
import com.vob.scanit.ui.CustomCameraPreview
import kotlinx.android.synthetic.main.activity_scan.*
import java.io.File

/* ScanActivity class defines methods to display text from the scanned image and
*  other functionality to process the images taken by the camera in the application  */
class ScanActivity: AppCompatActivity() {

    private val TAG = "ScanIt"
    private var imageSource = ""
    private var isCameraSource: Boolean = false
    private lateinit var imgURI: Uri
    var isImageTaken: Boolean = false
    var isResult: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        imageSource = intent.getStringExtra("Source").toString()

        when(imageSource) {
            "Camera" -> {
                val intent = Intent(this, CustomCameraPreview::class.java)
                startActivityForResult(intent, 111)
            }
            "Gallery" -> {
                progressBar.visibility = View.GONE
                startActivityForResult(Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI),112)
            }
            "Default" -> {
                progressBar.visibility = View.GONE
                CropImage.activity().start(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "onActivityResult")
        when(resultCode) {
            RESULT_OK -> when(requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri

                    if (isCameraSource) {
                        val originalUri = result.originalUri
                        val isDeleted = File(originalUri.path).delete()
                        Log.d("", "")
                    }


                    if (result.bitmap == null) {
                        val returnIntent = Intent()
                        returnIntent.putExtra("result", "File Not Found")
                        setResult(RESULT_OK, returnIntent)
                    }

                    imgURI = resultUri
                    isImageTaken = true
                    isResult = true
                    resultUri.path?.let { Log.d(TAG, it) }

                    val byteArray = readBytes(this, resultUri)

                    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                    progressBar.visibility = View.GONE
                    detectText(bitmap)
                }
                112 -> {
                    CropImage.activity(data?.data).start(this)
                }
                111 -> {
                    isCameraSource = true
                    val imageUri = Uri.parse(data?.getStringExtra("imagePath"))
                    CropImage.activity(imageUri).start(this)
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                val error = CropImage.getActivityResult(data).error

                setResult(RESULT_CANCELED)
                finish()
            }
            RESULT_CANCELED -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun readBytes(context: Context, uri: Uri): ByteArray? =
            context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }

    /*The following function processes the image to detect the text*/
    private fun detectText(scannedImage: Bitmap?)
    {
        var firebaseVisionImage: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(scannedImage!!)
        var firebaseApp = FirebaseApp.initializeApp(applicationContext)
        var firebaseVisionTextDetector = FirebaseVision.getInstance().onDeviceTextRecognizer
        firebaseVisionTextDetector.processImage(firebaseVisionImage)
                .addOnSuccessListener {
                    displayTextFromImage(it)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
    }

    /*displayTextFromImage() displays the text obtained from an image or an appropriate message when it
    * cannot decipher the text in the image*/
    private fun displayTextFromImage(firebaseVisionText: FirebaseVisionText) {
        //textView.text = ""
        var blockList: List<FirebaseVisionText.TextBlock> = firebaseVisionText.textBlocks
        if (blockList.size == 0)
        {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show()
        }
        else
        {
            blockList.forEach {
                var text = it.text
                processing_textView.text = text
                var lines = it.lines
                lines.forEach { line ->
                    if (processing_textView.text.toString() == "")
                    {
                        processing_textView.text = line.text
                        processing_textView.append("\n")
                    }
                    else
                    {
                        processing_textView.append(line.text)
                        processing_textView.append("\n")
                    }
                    //var elements = line.elements
                    //elements.forEach { element -> }
                }
            }
        }
    }

}