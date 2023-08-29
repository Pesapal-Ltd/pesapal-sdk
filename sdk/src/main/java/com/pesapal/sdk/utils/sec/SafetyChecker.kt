package com.pesapal.sdk.utils.sec

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.pesapal.sdk.App
import com.pesapal.sdk.utils.PrefManager

object SafetyChecker {

    fun checkDevice(){

    }





    /**
     * Returns Encrypted shared preference
     */
    private fun getEncryptedPref(): SharedPreferences?{
        return try {
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

            EncryptedSharedPreferences.create(
                PrefManager.MAP_PREF,
                masterKeyAlias,
                App.getInstance(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (exception: Exception) {
            null
        }
    }


    /**
     * Used to retrieve string from Encrypted Preferences
     */
    fun getString(preferenceName: String): String? {
        val encryptedSharedPreferences = getEncryptedPref() ?: return null
        return encryptedSharedPreferences.getString(preferenceName, null)
    }


    /**
     * Save sensitive credentials
     */
    fun putString(preferenceName: String, prefData: String){
        val encryptedSharedPreferences = getEncryptedPref()
        encryptedSharedPreferences?.edit()?.putString(preferenceName, prefData)?.apply()
    }


}