package com.pesapal.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import com.pesapal.sdk.utils.PrefManager
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec

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
    val P_KEY: String = "eff61ad8-d815-48d0-87e3-f548ef9d634e"

    fun readPublicKeyFromPreferences(): PublicKey? {
        return try {
            val publicKeyBase64: String = PrefManager.getString(getContextInstance(), P_KEY)
            if (publicKeyBase64 != "") {
                // Decode the Base64-encoded public key
                val publicKeyBytes = Base64.decode(publicKeyBase64, Base64.DEFAULT)
                // Create a PublicKey object from the byte array
                val keyFactory = KeyFactory.getInstance("RSA")
                val keySpec = X509EncodedKeySpec(publicKeyBytes)
                keyFactory.generatePublic(keySpec)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any exceptions that might occur during decoding or retrieval.
            null
        }
    }

    fun saveToPreferences(){

    }


}
