package com.pesapal.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.pesapal.sdk.Sdkapp;
import com.pesapal.sdk.BuildConfig;


public class PrefManager {

    private static final String TOKEN= "token";
    private static final String IPN_ID= "ipn_id";

    public static final String MAP_PREF = "826af370-30c6-4509-8c17-fd0096868b6" + BuildConfig.LIBRARY_PACKAGE_NAME;

    public static String con_key  = "7a85a0aa-a66f-427d-88af-4483e2877bc0";
    public static String con_sec  = "669339e8-a54e-4f95-99c7-91798109d756";
    public static String acc_num  = "334977dd-ff7e-4e26-8bd6-9488623e5404";
    public static String call_url = "71615ec2-324f-4a01-a555-4af0b8463048";
    public static String ipn_url  = "98f396e6-fb44-4265-ba18-518ff3864fb4";

    public static String PREF_IS_URL_LIVE = "021b44e3-33c3-4781-a29a-6637c4ccb10e";


    public static SharedPreferences getPreferences(Context context) {
//        return PreferenceManager.getDefaultSharedPreferences(Sdkapp.getInstance()
//                .getApplicationContext());
        return getEncryptedPref(context);
    }

    public static boolean getBoolean(Context context, String preferenceKey, boolean preferenceDefaultValue) {
        return getPreferences(context).getBoolean(preferenceKey, preferenceDefaultValue);
    }

    public static void putBoolean(Context context, String preferenceKey, boolean preferenceValue) {
        getPreferences(context).edit().putBoolean(preferenceKey, preferenceValue).apply();
    }

    public static String getString(Context context, String preferenceKey, String preferenceDefaultValue) {
        return getPreferences(context).getString(preferenceKey, preferenceDefaultValue);
    }

    public static void putString(Context context, String preferenceKey, String preferenceValue) {
        getPreferences(context).edit().putString(preferenceKey, preferenceValue).apply();
    }


    public static void setToken(Context context, String token){
        putString(context, TOKEN,token);
    }

    public static String getToken(Context context){
        return getString(context, TOKEN,null);
    }


    public static void setIpnId(Context context, String ipnId){
        putString(context, IPN_ID,ipnId);
    }

    public static String getIpnId(Context context){
        return getString(context, IPN_ID,null);
    }




    /**
     * Returns Encrypted shared preference
     */
    private static SharedPreferences getEncryptedPref(Context context){
        SharedPreferences sharedPreferences = null;
         try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

            sharedPreferences = EncryptedSharedPreferences.create(
                    PrefManager.MAP_PREF,
                    masterKeyAlias,
//                    Sdkapp.getInstance(),
                    context,
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
    public static void putStringEncrypted(Context context, String preferenceName, String prefData){
        SharedPreferences encryptedSharedPreferences = getEncryptedPref(context);
        if(encryptedSharedPreferences !=  null){
            encryptedSharedPreferences.edit().putString(preferenceName, prefData).apply();
        }
    }

    /**
     * Used to retrieve string from Encrypted Preferences
     */
    public static String getString(Context context,String preferenceName){
        SharedPreferences encryptedSharedPreferences = getEncryptedPref(context);
        if(encryptedSharedPreferences == null)
            return null;
        String data = encryptedSharedPreferences.getString(preferenceName, null);
        return  data;
    }


}
