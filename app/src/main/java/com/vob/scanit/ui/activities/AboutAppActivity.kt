package com.vob.scanit.ui.activities

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.vob.scanit.R


/*Inflates the layout that contains details about the app, such as version, theme, etc.*/
class AboutAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)


    }


}