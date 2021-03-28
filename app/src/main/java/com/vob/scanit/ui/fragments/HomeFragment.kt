package com.vob.scanit.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getbase.floatingactionbutton.FloatingActionButton
import com.monscanner.ScanActivity
import com.monscanner.ScanConstants
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.vob.scanit.ui.BottomSheetDialog
import com.vob.scanit.BuildConfig
import com.vob.scanit.PDFProcessing
import com.vob.scanit.R
import com.vob.scanit.adapters.PdfAdapterRv
import com.vob.scanit.ui.activities.DisplayPDFActivity
import org.jetbrains.annotations.Nullable
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

/*HomeFragment class handles opening the camera, checks for storage permissions, updates the list of
* documents, saves PDF files and begins scanning*/
class HomeFragment : Fragment(),PdfAdapterRv.PdfAdapterInterface {

    lateinit var openCameraButton: FloatingActionButton
    lateinit var openFilesButton: FloatingActionButton
    private val REQUEST_CODE = 7
    lateinit var pdfAdapterRv:PdfAdapterRv
    lateinit var recyclerView: RecyclerView
    lateinit var dir: File
    lateinit var listOfFiles: ArrayList<File>
    lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        initialiseFields(view)
    }

    /*onCreateView() inflates the layout and called the methods along with handling button clicks*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        checkForStoragePermissions()

        initialiseFields(view)

        updateListView()

        openCameraButton.setOnClickListener { openCamera(view) }
        openFilesButton.setOnClickListener { openGallery(view) }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val string = s.toString()
                pdfAdapterRv.filter(string)
            }
        })

        return view
    }

    private fun checkForStoragePermissions() {
        //camera permissions
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        }

        //storage permissions
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

   fun initialiseFields(view: View?) {
        openCameraButton = view?.findViewById(R.id.openCameraButton)!!
        openFilesButton = view.findViewById(R.id.openFilesButton)!!
        recyclerView = view.findViewById(R.id.recycler_view)
        dir = File(Environment.getExternalStorageDirectory().absolutePath, "Scanner")
        searchBar = view.findViewById(R.id.search_bar_HF)
    }

    fun updateListView() {
        dir = File(Environment.getExternalStorageDirectory().absolutePath, "Scanner")
        listOfFiles = getFiles(dir)
        listOfFiles.sort()

        pdfAdapterRv = activity?.let { context?.let { it1 -> PdfAdapterRv(it1, it,listOfFiles,this) } }!!
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = pdfAdapterRv
    }

    private fun openBottomSheet(position: Int) {
        val file = listOfFiles.get(position)

        val bottomSheetDialog = BottomSheetDialog(file)
        bottomSheetDialog.show(activity!!.supportFragmentManager, "Modal Bottom Sheet")

        val handler = Handler()
        handler.postDelayed({
            updateListView()
        }, 2000)
    }

    fun getFiles(dir: File): ArrayList<File> {
        val listFiles: Array<File>? = dir.listFiles()
        var fileList: ArrayList<File> = ArrayList()

        if (listFiles != null && listFiles.size > 0) {
            for (item in listFiles) {
                if (item.isDirectory) {
                    getFiles(item)
                } else {
                    var flag: Boolean = false
                    if (item.name.endsWith(".pdf")) {
                        for (element in fileList) {
                            if (element.name.equals(item.name)) {
                                flag = true
                            }
                        }
                        if (flag) {
                            flag = false
                        } else {
                            fileList.add(item)
                        }
                    }
                }
            }
        }
        return fileList
    }

    fun openCamera(view: View?) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        } else {
            startScan(ScanConstants.OPEN_CAMERA)
        }
    }

    fun openGallery(view: View?) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            startScan(ScanConstants.OPEN_GALERIE)
        }
    }

    fun startScan(preference: Int) {
        val intent: Intent = Intent(context!!, ScanActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                try {
                    if (BuildConfig.DEBUG && data == null) {
                        error("Assertion failed")
                    }
                    val imageUri: Uri = Objects.requireNonNull(data!!.extras)?.getParcelable(ScanActivity.SCAN_RESULT)!!
                    val imageStream: InputStream = activity!!.contentResolver.openInputStream(imageUri)!!
                    val scannedImage = BitmapFactory.decodeStream(imageStream)
                    activity!!.contentResolver.delete(imageUri, null, null)
                    getNameAndSavePDF(scannedImage)

                    //PDFProcessing().makePDF(scannedImage, filename)
                    //Toast.makeText(context, "Successful! PATH: Internal Storage/${Environment.getExternalStorageDirectory().absolutePath}/Scanner".trimIndent(), Toast.LENGTH_SHORT).show()


                    //Toast.makeText(context, "Null filename", Toast.LENGTH_SHORT).show()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getNameAndSavePDF(scannedImage: Bitmap) {
        var name: String = ""
        val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        val mView: View = layoutInflater.inflate(R.layout.filename_dialog, null)
        val txt_inputText: EditText = mView.findViewById<View>(R.id.txt_input) as EditText
        val btn_cancel: Button = mView.findViewById<View>(R.id.btn_cancel) as Button
        val btn_okay: Button = mView.findViewById<View>(R.id.btn_okay) as Button
        val error_msg: TextView = mView.findViewById(R.id.error_msg)

        var errorCode: Boolean = true
        alert.setView(mView)
        val alertDialog: android.app.AlertDialog? = alert.create()
        alertDialog?.setCanceledOnTouchOutside(false)
        btn_cancel.setOnClickListener(View.OnClickListener {
            name = "#123#..456"
            alertDialog?.dismiss()
        })
        btn_okay.setOnClickListener(View.OnClickListener {
            name = txt_inputText.text.toString()
            val filelist = getFiles(dir)
            for (file in filelist) {
                if (file.name.equals("$name.pdf")) {
                    DynamicToast.makeWarning(context!!, "File with the given name already exists.", Toast.LENGTH_LONG).show()
                    //Toast.makeText(context, "Error: File with the given name already exists.", Toast.LENGTH_SHORT).show()
                    error_msg.visibility = View.VISIBLE
                    errorCode = false
                }
            }
            if (errorCode) {
                Toast.makeText(context, "Generating PDF...", Toast.LENGTH_SHORT).show()
                PDFProcessing(requireContext()).makePDF(scannedImage, name)
            }
            alertDialog?.dismiss()

            val handler = Handler()
            handler.postDelayed({
                updateListView()
            }, 1000)
        })
        alertDialog?.show()
    }

    override fun onItemClicked(position: Int) {
        val file = listOfFiles[position]

        val intent = Intent(context?.applicationContext, DisplayPDFActivity::class.java)
        intent.putExtra("uri", file.toURI().toString())
        intent.putExtra("filename", file.name)
        startActivity(intent)
    }

    override fun onItemLongClicked(position: Int) {
        openBottomSheet(position)
    }

}
