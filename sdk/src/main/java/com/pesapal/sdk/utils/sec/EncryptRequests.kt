package com.pesapal.sdk.utils.sec

import android.util.Base64
import android.util.Log
import com.pesapal.sdk.Sdkapp
import javax.crypto.Cipher

object EncryptRequests {

    fun encryptWithPublicKey(requestData: String): String {
        var publicKey = Sdkapp.readPublicKeyFromPreferences();
        // Step 2: Convert the request data to bytes
        val requestDataBytes = requestData.toByteArray(Charsets.UTF_8)
        // Step 3: Encrypt the request data using the RSA public key
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(requestDataBytes)
        // Step 4: Encode the encrypted data to Base64
        val encryptedRequest = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        return encryptedRequest
    }

}