package com.pesapal.sdk.utils;

public class PESAPALAPI3SDK {

    public static String AMOUNT     = "a52a84e4-deac-412b-9f16-0d18ab65ca2e";
    public static String ORDER_ID   = "80887a91-9e38-4c26-982c-64e976ed1583";
    public static String CURRENCY   = "3d9f164d-977b-4fb0-9b8f-2f558f7ba587";
    public static String FIRST_NAME = "aea1c6c5-f69f-4af9-96ef-dd463d62379d";
    public static String LAST_NAME  = "0fc5d353-ce20-400f-86ac-3ce3ac4b88ef";
    public static String EMAIL      = "ed0d5168-0ef7-46d2-ae4b-5466cf037232";

    public static String CURRENCY_CODE_KES      = "KES";
    public static String CURRENCY_CODE_USD      = "USD";
    public void init(String consumerkey, String consumersecret,String accountNumber, String callbackUrl, String ipnUrl){
//         PrefManager.putStringEncrypted("consumer_key", consumerkey);
//         PrefManager.putStringEncrypted("consumer_secret", consumersecret);
//         PrefManager.putStringEncrypted("account_number", accountNumber);
//         PrefManager.putStringEncrypted("callback_url", callbackUrl);
//         PrefManager.putStringEncrypted("ipn_url", ipnUrl);

        PrefManager.putStringEncrypted(PrefManager.con_key, consumerkey);
        PrefManager.putStringEncrypted(PrefManager.con_sec, consumersecret);
        PrefManager.putStringEncrypted(PrefManager.acc_num, accountNumber);
        PrefManager.putStringEncrypted(PrefManager.call_url, callbackUrl);
        PrefManager.putStringEncrypted(PrefManager.ipn_url, ipnUrl);
    }
}
