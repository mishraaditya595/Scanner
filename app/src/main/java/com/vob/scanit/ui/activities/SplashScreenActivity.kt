package com.vob.scanit.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.rbddevs.splashy.Splashy
import com.vob.scanit.R

/*SplashScreenActivity class provides animations and other customisations when the app logo is loaded*/
class SplashScreenActivity : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Splashy(this)
                .setLogo(R.mipmap.scanit_icon_round)
                .setTitle("ScanIt")
                .setTitleColor("#FFFFFF")
                .setSubTitle("Your All-In-One Scanner")
                .setSubTitleColor("#FFFFFF")
                .setProgressColor(R.color.white)
                .setBackgroundColor(R.color.colorPrimaryDark)
                .setAnimation(Splashy.Animation.SLIDE_IN_LEFT_BOTTOM, 1300)
                .setFullScreen(true)
                .setDuration(3000)
                .setFullScreen(true)
                .show()

        handler = Handler()
        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        },3000)
    }
}