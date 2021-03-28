package com.vob.scanit.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.ClipboardManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.monscanner.ScanActivity
import com.monscanner.ScanConstants
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.vob.scanit.R
import java.io.File
import java.io.InputStream
import java.util.*

/*OCRFragment handles all the functions related to optical character recognition*/
class OCRFragment : Fragment() {

    companion object{
        const val REQUEST_CODE = 52525
    }

    //private val TAG: String? = getActivity()?.javaClass?.simpleName
    //public val TESS_DATA: String = "/tessdata"
    //lateinit var button: Button
    lateinit var textView: TextView
    lateinit var copyToClipboardBtn: Button
    lateinit var instruction_text: TextView
    lateinit var openCameraButton: FloatingActionButton
    lateinit var openFilesButton: FloatingActionButton
    lateinit var outputFileDir: Uri
    private var isAuthorised = false
    val DATA_PATH: String = android.os.Environment.getExternalStorageDirectory().absolutePath + "/Scanner/Tess"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_o_c_r, container, false)

        initialiseFields(view)
        FirebaseApp.initializeApp(requireContext())

        openCameraButton.setOnClickListener { openCamera(view) }
        openFilesButton.setOnClickListener { openGallery(view) }

        if (textView.text.isEmpty())
        {
            instruction_text.visibility = View.VISIBLE
        }

        copyToClipboardBtn.setOnClickListener { copyTextToClipboard() }

        return view
    }

    private fun initialiseFields(view: View) {
        textView = view.findViewById(R.id.ocr_tv)
        openCameraButton = view.findViewById(R.id.openCameraButton_ocr)!!
        openFilesButton = view.findViewById(R.id.openFilesButton_ocr)!!
        instruction_text = view.findViewById(R.id.ocr_instruction_tv)
        copyToClipboardBtn = view.findViewById(R.id.copy_button)
    }

    private fun openCamera(view: View){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    2)
        }
        else{
            val intent = Intent(activity, com.vob.scanit.ui.activities.ScanActivity::class.java)
            intent.putExtra("Source","Camera")
            startActivityForResult(intent, REQUEST_CODE)
        }
    }
    /*private fun openCamera(view: View?) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    2)
        }
        else
        {
            startScan(ScanConstants.OPEN_CAMERA)
        }
    }*/

    private fun openGallery(view: View?) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 2)
        }
        else
        {
            val intent = Intent(activity, com.vob.scanit.ui.activities.ScanActivity::class.java)
            intent.putExtra("Source","Gallery")
            startActivityForResult(intent, REQUEST_CODE)
            //startScan(ScanConstants.OPEN_GALERIE)
        }
    }

    fun startScan(preference: Int) {
        val imagePath = DATA_PATH + "/image"
        val dir = File(imagePath)
        if (!dir.exists())
            dir.mkdirs()
        val imageFilePath = imagePath + "/ocr.jpg"
        outputFileDir = Uri.fromFile(File(imageFilePath))
        val intent: Intent = Intent(requireContext(), ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
        startActivityForResult(intent, 100)
    }

    private fun startCameraActivity() {
        try {
            val imagePath = DATA_PATH + "/image"
            val dir = File(imagePath)
            if (!dir.exists())
                dir.mkdirs()
            val imageFilePath = imagePath + "/ocr.jpg"
            outputFileDir = Uri.fromFile(File(imageFilePath))
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileDir)
            //if (intent.resolveActivity(activity!!.packageManager) != null)
                //startActivityForResult(intent, 100)
        }
        catch (e: Exception) {
            Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE){
            if (isAuthorised&&resultCode== Activity.RESULT_OK){
                val returnIntent = Intent("com.ba.etab.action.CUSTOM_INTENT")
                returnIntent.putExtra("result",data!!.getStringExtra("result").toString())
                requireActivity().setResult(Activity.RESULT_OK,returnIntent)
                requireActivity().finish()
            }

            if (resultCode == Activity.RESULT_CANCELED){
                requireActivity().setResult(Activity.RESULT_CANCELED)
                requireActivity().finish()
            }
        }
    }

    private fun detectText(scannedImage: Bitmap?)
    {
        var firebaseVisionImage: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(scannedImage!!)
        var firebaseApp = FirebaseApp.initializeApp(requireContext())
        var firebaseVisionTextDetector = FirebaseVision.getInstance().onDeviceTextRecognizer
        firebaseVisionTextDetector.processImage(firebaseVisionImage)
                .addOnSuccessListener {
                    displayTextFromImage(it)
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
    }

    private fun displayTextFromImage(firebaseVisionText: FirebaseVisionText) {
        textView.text = ""
        var blockList: List<FirebaseVisionText.TextBlock> = firebaseVisionText.textBlocks
        if (blockList.size == 0)
        {
            Toast.makeText(activity, "No text found", Toast.LENGTH_SHORT).show()
        }
        else
        {
            blockList.forEach {
                /*var text = it.text
                textView.text = text*/
                var lines = it.lines
                lines.forEach { line ->
                    if (textView.text.toString() == "")
                    {
                        textView.text = line.text
                        textView.append("\n")
                    }
                    else
                    {
                        textView.append(line.text)
                        textView.append("\n")
                    }
                    //var elements = line.elements
                    //elements.forEach { element -> }
                }
            }
        }
        if (textView.text.isNotEmpty())
        {
            instruction_text.visibility = View.INVISIBLE
        }
    }

    private fun copyTextToClipboard() {
        if (textView.text.isEmpty())
        {
            DynamicToast.makeError(requireContext(), "Error: No text found.", Toast.LENGTH_LONG).show()
            return
        }

        var clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        var clip = ClipData.newPlainText("Copied Text", textView.text)

        //clipboard.primaryClip = clip
        DynamicToast.makeSuccess(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}