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
    public static String USER_DATA = "aea1c6c5-f69f-4af9-96ef-dd463d62379d";

    public static String CURRENCY_CODE_KES      = "KES";
    public static String CURRENCY_CODE_UGX      = "UGX";
    public static String CURRENCY_CODE_TZS      = "TZS";
    public static String CURRENCY_CODE_USD      = "USD";

    public enum COUNTRIES_ENUM {
        COUNTRY_KE, COUNTRY_TZ, COUNTRY_UG
    }




    public static void init(Context context, String consumerkey, String consumersecret, String accountNumber, String callbackUrl, String ipnUrl, boolean isLive){
        Sdkapp.INSTANCE.setContextInstance(context);
        PrefManager.firstSave(context, consumerkey, consumersecret, accountNumber, callbackUrl, ipnUrl, isLive);
    }

    public static String ERR_SECURITY = "11229719-8e5b-4481-a005-9da49583bc77";
    public static String ERR_GENERAL  = "9b0a5988-3430-4d4f-b289-3ad1e69c4dd2";
    public static String ERR_NETWORK  = "a91f31b8-6c6b-47ff-9b85-936644f198be";
    public static String ERR_INIT     = "838e5f5d-9482-432b-85ac-37b393bd514c";


    public static String STATUS_PENDING   = "d9d60510-f2b5-4f1d-bb73-fe16a9b7322a";
    public static String STATUS_COMPLETED = "ffb4aeb8-3daf-4886-96ad-cf4836f13ff5";
    public static String STATUS_CANCELLED = "dab02b17-d9c0-4240-97ee-b8c44437d4c0";



}


