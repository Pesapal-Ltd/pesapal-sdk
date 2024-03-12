package com.pesapal.sdkdemo.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pesapal.sdkdemo.AppDemo;


public class PrefManager {

    private static final String CURRENCY= "KES";
    private static final String CONSUMER_KEY= "CONSUMER_KEY";
    private static final String CONSUMER_SECRET= "CONSUMER_SECRET";
    private static final String CALL_BACK_URL = "CALL_BACK_URL";
    private static final String ACCOUNT = "ACCOUNT";
    private static final String IS_PRODUCTION = "IS_PRODUCTION";
    private static final String COUNTRY ="COUNTRY";
    public static final String PREF_FIRST_NAME ="PREF_FIRST_NAME";
    public static final String PREF_LAST_NAME ="PREF_LAST_NAME";
    public static final String PREF_EMAIL ="PREF_EMAIL";
    public static final String PREF_PHONE ="PREF_PHONE";


    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(AppDemo.Companion.getInstance());
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
        return getString(CONSUMER_KEY, "");
    }

    public static void setConsumerSecret(String consumerSecret){
        putString(CONSUMER_SECRET, consumerSecret);
    }

    public static String getConsumerSecret(){
        return getString(CONSUMER_SECRET,"");
    }

    public static Boolean getIsProduction(){
        return getBoolean(IS_PRODUCTION, false);
    }

    public static void setIsProduction(boolean isProd){
        putBoolean(IS_PRODUCTION, isProd);
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

    public static String getCountry(){
        return getString(COUNTRY, "");
    }
    public static void setCountry(String country){
         putString(COUNTRY, country);
    }


}
