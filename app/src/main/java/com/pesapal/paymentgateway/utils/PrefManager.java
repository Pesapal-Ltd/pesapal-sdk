package com.pesapal.paymentgateway.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pesapal.paygateway.activities.payment.App;

public class PrefManager {

    private static final String CURRENCY= "KES";
    private static final String CONSUMER_KEY= "CONSUMER_KEY";
    private static final String CONSUMER_SECRET= "CONSUMER_SECRET";
    private static final String CALL_BACK_URL = "CALL_BACK_URL";
    private static final String ACCOUNT = "ACCOUNT";

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


    public static void setCurrency(String currency){
        putString(CURRENCY,currency);
    }

    public static String getCurrency(){
        return getString(CURRENCY,"KES");
    }

    public static void setConsumerKey(String consumerKey){
        putString(CONSUMER_KEY, consumerKey);
    }

    public static String getConsumerKey(){
        return getString(CONSUMER_KEY, "qkio1BGGYAXTu2JOfm7XSXNruoZsrqEW");
    }

    public static void setConsumerSecret(String consumerSecret){
        putString(CONSUMER_SECRET, consumerSecret);
    }

    public static String getConsumerSecret(){
        return getString(CONSUMER_SECRET,"osGQ364R49cXKeOYSpaOnT++rHs=");
    }

    public static void setCallBackUrl(String callBackUrl){
        putString(CALL_BACK_URL, callBackUrl);
    }

    public static String getCallBackUrl(){
        return getString(CALL_BACK_URL, "http://localhost:56522");
    }

    public static void setAccount(String account){
        putString(ACCOUNT, account);
    }

    public static String getAccount(){
        return getString(ACCOUNT, "1000101");
    }



}
