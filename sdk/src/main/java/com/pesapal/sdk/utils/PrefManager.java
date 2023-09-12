package com.pesapal.sdk.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.pesapal.sdk.App;


public class PrefManager {

    private static final String TOKEN= "token";
    private static final String IPN_ID= "ipn_id";

    public static final String MAP_PREF = "826af370-30c6-4509-8c17-fd0096868b60";

    public static String con_key  = "7a85a0aa-a66f-427d-88af-4483e2877bc0";
    public static String con_sec  = "669339e8-a54e-4f95-99c7-91798109d756";
    public static String acc_num  = "334977dd-ff7e-4e26-8bd6-9488623e5404";
    public static String call_url = "71615ec2-324f-4a01-a555-4af0b8463048";
    public static String ipn_url  = "98f396e6-fb44-4265-ba18-518ff3864fb4";

    public static String PREF_IS_URL_LIVE = "021b44e3-33c3-4781-a29a-6637c4ccb10e";


    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()
                .getApplicationContext());
    }

    public static int getInt(String preferenceKey, int preferenceDefaultValue) {
        return getPreferences().getInt(preferenceKey, preferenceDefaultValue);
    }


    public static void putInt(String preferenceKey, int preferenceValue) {
        getPreferences().edit().putInt(preferenceKey, preferenceValue).apply();
    }

    public static boolean getBoolean(String preferenceKey, boolean preferenceDefaultValue) {
        return getPreferences().getBoolean(preferenceKey, preferenceDefaultValue);
    }

    public static void putBoolean(String preferenceKey, boolean preferenceValue) {
        getPreferences().edit().putBoolean(preferenceKey, preferenceValue).apply();
    }

    public static String getString(String preferenceKey, String preferenceDefaultValue) {
        return getPreferences().getString(preferenceKey, preferenceDefaultValue);
    }

    public static void putString(String preferenceKey, String preferenceValue) {
        getPreferences().edit().putString(preferenceKey, preferenceValue).apply();
    }


    public static void setToken(String token){
        putString(TOKEN,token);
    }

    public static String getToken(){
        return getString(TOKEN,null);
    }


    public static void setIpnId(String ipnId){
        putString(IPN_ID,ipnId);
    }

    public static String getIpnId(){
        return getString(IPN_ID,null);
    }




    /**
     * Returns Encrypted shared preference
     */
    private static SharedPreferences getEncryptedPref(){
        SharedPreferences sharedPreferences = null;
         try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

            sharedPreferences = EncryptedSharedPreferences.create(
                    PrefManager.MAP_PREF,
                    masterKeyAlias,
                    App.getInstance(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception ignored) {

        }

        return sharedPreferences;
    }



    /**
     * Save sensitive credentials
     */
    public static void putStringEncrypted(String preferenceName, String prefData){
        SharedPreferences encryptedSharedPreferences = getEncryptedPref();
        if(encryptedSharedPreferences !=  null){
            encryptedSharedPreferences .edit().putString(preferenceName, prefData).apply();
        }
    }

    /**
     * Used to retrieve string from Encrypted Preferences
     */
    public static String getString(String preferenceName){
        SharedPreferences encryptedSharedPreferences = getEncryptedPref();
        if(encryptedSharedPreferences == null)
            return null;
        String data = encryptedSharedPreferences.getString(preferenceName, null);
        return  data;
    }


}
