package com.pesapal.sdk.utils.sec

import android.util.Base64
import com.pesapal.sdk.Sdkapp
import com.pesapal.sdk.utils.PrefManager
import java.security.PublicKey

object ParseUtil {
    fun parsePublicKey(publicKey: PublicKey): Boolean {
        val publicKeyBytes = publicKey.encoded
        // Encode the byte array using Base64
        val publicKeyBase64 = Base64.encodeToString(publicKeyBytes, Base64.DEFAULT)
        PrefManager.putString(Sdkapp.getContextInstance(), Sdkapp.P_KEY, publicKeyBase64)
        return true
    }

}