package com.vob.scanit

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

/*This kotlin file defines the themes for the app, which allows the user to toggle between system theme,
* light theme and dark theme*/

fun currentSystemTheme(context: Context):Int{
    return when(context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)){
        Configuration.UI_MODE_NIGHT_NO -> 1
        Configuration.UI_MODE_NIGHT_YES -> 2
        else -> 0
    }
}

fun setAppTheme(i:Int){
    when (i){

        //0 for system theme
        //1 for user light theme
        //2 for user dark theme

        1 -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
        2 -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)}
        0 -> {}
    }
}