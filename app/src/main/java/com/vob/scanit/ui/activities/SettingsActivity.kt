package com.vob.scanit.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vob.scanit.R
import com.vob.scanit.ui.fragments.SettingsFragment

/*SettingsActivity class calls the SettingsFragment() to display the list of options available to the user*/
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SettingsTheme)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
    }
}
