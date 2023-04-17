package com.pesapal.sdk.utils;

import java.io.IOException;

public class RetrofitErrorUtil {

    public static String serverException(Throwable t){
        if (t instanceof IOException) {
            return "NETWORK ERROR, PLEASE CHECK YOUR WIFI OR DATA CONNECTION ";
        } else {
            return " Unable to process request, Kindly try again later ";
        }
    }


}
