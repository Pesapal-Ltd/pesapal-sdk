package com.pesapal.sdk.utils.sec

import android.util.Base64
import android.util.Log
import com.pesapal.sdk.Sdkapp
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException


object EncryptRequests {

    fun encryptWithPublicKey(requestData: String): String {
        var publicKey = Sdkapp.readPublicKeyFromPreferences();
        // Step 2: Convert the request data to bytes
        val requestDataBytes = requestData.toByteArray(Charsets.UTF_8)
        // Step 3: Encrypt the request data using the RSA public key
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
//        val cipher = Cipher.getInstance("RSA/NONE/NOPADDING")
//        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
//        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")// Error aes cant be used with rsa
//        val cipher = Cipher.getInstance("RSA/ECB/NOPADDING")
//        val cipher = Cipher.getInstance("RSA/ECB/PKCS5Padding")


        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(requestDataBytes)
        // Step 4: Encode the encrypted data to Base64
        val encryptedRequest = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        return encryptedRequest
    }

    val TAG ="ENcry"
    fun encrypt(data: String): String {
        var publicKey = Sdkapp.readPublicKeyFromPreferences();

        Log.d(TAG, "Encryption begins")
        val mEncryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        mEncryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val payload = data.toByteArray(StandardCharsets.UTF_8)
        val payloadLength = payload.size
        val blockSize: Int = mEncryptCipher.getBlockSize()
        Log.d(TAG, "blockSize: $blockSize; payloadLength: $payloadLength")
        val stream = ByteArrayOutputStream()
        var start = 0
        var end = 0
        while (end < payloadLength) {
            end = end + blockSize
            if (end > payloadLength) {
                end = payloadLength
            }
            Log.d(TAG, "start: " + start + "; end: " + end + "; block: " + (end - start))
            start = try {
                val encryptedSlice: ByteArray = mEncryptCipher.doFinal(
                    payload, start,
                    end - start
                )
                Log.d(TAG, "Encrypted Slice Length: " + encryptedSlice.size)
                stream.write(encryptedSlice)
                end
            } catch (e: IOException) {
                Log.e(TAG, "update failed")
                e.printStackTrace()
                break
            } catch (e: BadPaddingException) {
                Log.e(TAG, "update failed")
                e.printStackTrace()
                break
            } catch (e: IllegalBlockSizeException) {
                Log.e(TAG, "update failed")
                e.printStackTrace()
                break
            }
        }
        val encryptedPayload = stream.toByteArray()
        try {
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d(TAG, "encryptedPayload length: " + encryptedPayload.size)
        val base64EncryptedPayload: ByteArray =Base64.encode(encryptedPayload, Base64.DEFAULT)
        Log.d(TAG, "base64EncryptedPayload length: " + base64EncryptedPayload.size)
        Log.d(TAG, "Encryption ends")
        return base64EncryptedPayload.toString(Charsets.UTF_8)
    }

}