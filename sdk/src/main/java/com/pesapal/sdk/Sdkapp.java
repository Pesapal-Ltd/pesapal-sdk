package com.pesapal.sdk;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
public class Sdkapp extends Application {
    private static Context mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = getApplicationContext();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static synchronized Context getInstance() {
        if (mInstance == null) {
            mInstance = new Sdkapp();
        }
        return mInstance;
    }

}
