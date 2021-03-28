package com.vob.scanit.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.vob.scanit.R

/*The following class processes the data obtained from the QR*/
class ShowQRData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_q_r_data)

        val data = intent.getStringExtra("data")
        val tv = findViewById<TextView>(R.id.tv)
        if (data != null)
        {
            tv.text = data
        }
        else
            tv.text = "Nothing found"

    }
}