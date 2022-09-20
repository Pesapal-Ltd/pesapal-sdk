package com.pesapal.paymentgateway.payment.utils;

import java.io.IOException;

public class RetrofitErrorUtil {

    public static String serverError(int code, String message){
        if (code >= 200 && code < 300) {
            return "SUCCESS! " + message;
        } else if (code == 401) {
            return "Unable to process request, Kindly try again later";
        } else if (code >= 400 && code < 500) {
            return "Unable to process request, Kindly try again later ";
        } else if (code >= 500 && code < 600) {
            return "Unable to process request, Kindly try again later";
        } else {
            return "Unable to process request, Kindly try again later";
        }
    }

    public static String serverException(Throwable t){
        if (t instanceof IOException) {
            return "NETWORK ERROR, PLEASE CHECK YOUR WIFI OR DATA CONNECTION ";
        } else {
            return " Unable to process request, Kindly try again later ";
        }
    }


}
