package com.vob.scanit.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.vob.scanit.R

/*QRBarcodeScanResultActivity class provides options to a user to process the data generated from the QR */
class QRBarcodeScanResultActivity : AppCompatActivity() {

    lateinit var scanData: String
    lateinit var openInBrowserBtn: Button
    lateinit var shareBtn: Button
    lateinit var scanResultTV: TextView

    /*onCreate() handles button clicks, where the user can select to share the data obtained or open it in a browser*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_barcode_scan_result)

        scanData = intent.getStringExtra("result")!!

        initialiseFields()

        setupToolbar()

        scanResultTV.text = scanData

        openInBrowserBtn.setOnClickListener {openInBrowser()}
        shareBtn.setOnClickListener { share() }
    }

    /*The class variables are initialised*/
    private fun initialiseFields() {
        openInBrowserBtn = findViewById(R.id.openInBrowser_btn)
        scanResultTV = findViewById(R.id.scanResult_tv)
        shareBtn = findViewById(R.id.share_btn)
    }

    /*When the user chooses to open the results of the scan in the browser, the following function is called*/
    private fun openInBrowser() {
        val isUrl: Boolean = URLUtil.isValidUrl(scanData)
        if (isUrl)
        {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(scanData)
            startActivity(openURL)
        }
        else
        {
            showAlertDialog()
        }
    }

    /*When the user chooses to share the QR data, the share() function opens an app chooser to allow the
      user to choose from the options available*/
    private fun share() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, scanData)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    /*When the user selects the option to open the QR data in a browser, and the data generated is not an
    * URL, we display appropriate messages*/
    private fun showAlertDialog() {
        val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        val mView: View = layoutInflater.inflate(R.layout.filename_dialog, null)

        val txt_inputText: EditText = mView.findViewById<View>(R.id.txt_input) as EditText
        val btn_cancel: Button = mView.findViewById<View>(R.id.btn_cancel) as Button
        val btn_okay: Button = mView.findViewById<View>(R.id.btn_okay) as Button
        val header = mView.findViewById<View>(R.id.heading) as TextView

        header.text = "The data extracted seems not to be URL"
        txt_inputText.visibility = View.GONE
        btn_okay.text = "Open URL"
        btn_cancel.text = "Close"

        alert.setView(mView)
        val alertDialog: android.app.AlertDialog? = alert.create()
        alertDialog?.setCanceledOnTouchOutside(false)
        btn_cancel.setOnClickListener(View.OnClickListener {
            alertDialog?.dismiss()
        })
        btn_okay.setOnClickListener(View.OnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://$scanData")
            startActivity(openURL)
        })
        alertDialog?.show()
    }

    /*setupToolbar() sets up the toolbar*/
    private fun setupToolbar() {
        var toolbar = findViewById<Toolbar>(R.id.showdata_toolbar)
        toolbar.title = "Scan Result"
        toolbar.setTitleTextAppearance(applicationContext, R.style.TextAppearance_AppCompat_Title)
        toolbar.setTitleTextColor(-0x1)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}