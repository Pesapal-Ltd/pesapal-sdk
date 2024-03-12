package com.pesapal.sdk.utils;

import android.content.Context;

import com.pesapal.sdk.Sdkapp;

public class PESAPALAPI3SDK {
    private PESAPALAPI3SDK() {
    }

    public static String AMOUNT     = "a52a84e4-deac-412b-9f16-0d18ab65ca2e";
    public static String ORDER_ID   = "80887a91-9e38-4c26-982c-64e976ed1583";
    public static String CURRENCY   = "3d9f164d-977b-4fb0-9b8f-2f558f7ba587";
    public static String COUNTRY    = "c9de05ef-afd4-4070-8cbb-301223f7c515";
    public static String FIRST_NAME = "aea1c6c5-f69f-4af9-96ef-dd463d62379d";
    public static String LAST_NAME  = "0fc5d353-ce20-400f-86ac-3ce3ac4b88ef";
    public static String EMAIL      = "ed0d5168-0ef7-46d2-ae4b-5466cf037232";

//    public static String CURRENCY_CODE_KES      = "KES";
//    public static String CURRENCY_CODE_USD      = "USD";

//    public static String COUNTRY_KE = "KE";
//    public static String COUNTRY_TZ = "TZ";
//    public static String COUNTRY_UG = "UG";

    public enum COUNTRIES_ENUM {
        COUNTRY_KE, COUNTRY_TZ, COUNTRY_UG
    }


    public static void init(Context context, String consumerkey, String consumersecret, String accountNumber, String callbackUrl, String ipnUrl, boolean isLive){
        Sdkapp.INSTANCE.setContextInstance(context);
        PrefManager.firstSave(context, consumerkey, consumersecret, accountNumber, callbackUrl, ipnUrl, isLive);
    }

}


