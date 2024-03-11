package com.pesapal.sdk;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
public class Sdkapp extends Application {
    private static Sdkapp mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static synchronized Sdkapp getInstance() {
        if (mInstance == null) {
            mInstance = new Sdkapp();
        }
        return mInstance;
    }

}
