package com.pesapal.sdk.utils;

public class PESAPALAPI3SDK {

    public void init(String consumerkey, String consumersecret,String accountNumber, String callbackUrl, String ipnUrl){
         PrefManager.putString("consumer_key", consumerkey);
         PrefManager.putString("consumer_secret", consumersecret);
         PrefManager.putString("account_number", accountNumber);
         PrefManager.putString("callback_url", callbackUrl);
         PrefManager.putString("ipn_url", ipnUrl);
    }
}
