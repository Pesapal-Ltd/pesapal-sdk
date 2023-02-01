package com.pesapal.paygateway.activities.payment;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {
    private static App mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static App getContext() {
        if (mInstance == null) {
            mInstance = new App();
        }
        return mInstance;
    }

    public static synchronized App getInstance() {
        if (mInstance == null) {
            mInstance = new App();
        }
        return mInstance;
    }




}
