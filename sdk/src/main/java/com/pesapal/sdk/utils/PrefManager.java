package com.pesapal.sdk.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.pesapal.sdk.App;


public class PrefManager {

    private static final String TOKEN= "token";
    private static final String IPN_ID= "ipn_id";

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


}
