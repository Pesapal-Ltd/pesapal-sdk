package com.pesapal.paymentgateway.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pesapal.paygateway.activities.payment.App;

public class PrefManager {

    private static final String ORDER_ID= "order_id";

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


    public static void setOrderId(String orderId){
        putString(ORDER_ID,orderId);
    }

    public static String getOrderId(){
        return getString(ORDER_ID,"1");
    }


}
