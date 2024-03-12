package com.pesapal.sdk

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object Sdkapp{


    // TODO INVESTIGATE STATIC CONTEXT LEAK in Sdkapp. It will happen eventually


    private var mInstance: Context? = null

    fun setContextInstance(context: Context){
        mInstance = context
    }
    fun getContextInstance(): Context? = mInstance

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
