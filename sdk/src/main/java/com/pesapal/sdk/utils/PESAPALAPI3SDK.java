package com.pesapal.sdk.utils;

public class PESAPALAPI3SDK {

    public void init(String consumerkey, String consumersecret,String accountNumber, String callbackUrl, String ipnUrl){
         PrefManager.putStringEncrypted("consumer_key", consumerkey);
         PrefManager.putStringEncrypted("consumer_secret", consumersecret);
         PrefManager.putStringEncrypted("account_number", accountNumber);
         PrefManager.putStringEncrypted("callback_url", callbackUrl);
         PrefManager.putStringEncrypted("ipn_url", ipnUrl);
    }
}
