package com.vob.scanit.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
//import com.blikoon.qrcodescanner.QrCodeActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.vob.scanit.R
import com.vob.scanit.ui.activities.GenerateQRActivity
import com.vob.scanit.ui.activities.QRBarcodeScanResultActivity
//import com.google.zxing.integration.android.IntentIntegrator
//import com.vob.scanit.R
import com.vob.scanit.ui.activities.ShowQRData

/*QRFragment class overrides methods that handle inflating the layout, creating the views, releasing
* camera when not in use and starts the preview surface for the camera */
class QRFragment : Fragment() {

    //private val REQUEST_CODE_QR_SCAN = 101
    //lateinit var scannerView: ScannerView
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_q_r, container, false)


        val generateQRBtn: Button = view.findViewById(R.id.generate_qr_btn)
        generateQRBtn.setOnClickListener {
            val intent = Intent(context, GenerateQRActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanView)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                val intent = Intent(activity, QRBarcodeScanResultActivity::class.java)
                intent.putExtra("result", it.text)
                startActivity(intent)
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}