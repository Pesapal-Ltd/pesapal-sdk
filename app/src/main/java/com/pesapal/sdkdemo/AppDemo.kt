package com.pesapal.sdkdemo

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class AppDemo : Application() {
    override fun onCreate() {
        super.onCreate()
        mInstance = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    companion object {
        private var mInstance: Context? = null
        fun getInstance(): Context? = mInstance

//        @JvmStatic
//        @get:Synchronized
//        val instance: Context?
//            get() {
//                if (mInstance == null) {
//                    mInstance = Sdkapp()
//                }
//                return mInstance
//            }


    }
}
