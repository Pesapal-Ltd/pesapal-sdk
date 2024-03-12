package com.pesapal.sdk.data.api

import android.content.Context
import com.pesapal.sdk.BuildConfig
import com.pesapal.sdk.Configs
import com.pesapal.sdk.Sdkapp
import com.pesapal.sdk.data.services.ApiServices
import com.pesapal.sdk.utils.PrefManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val retrofit: Retrofit
        get() {
            return Retrofit.Builder()
                .baseUrl(getBaseUrl(Sdkapp.getContextInstance()))
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build()
        }

    internal val apiServices: ApiServices get() = retrofit.create(ApiServices::class.java)


    private fun getOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
        if(BuildConfig.DEBUG) {
            builder.addInterceptor(interceptor)
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .build()
                    chain.proceed(newRequest)
                }
        }
        return builder.build()
    }

    private fun getBaseUrl(context: Context?): String{
        val isLive = PrefManager.getBoolean(context, PrefManager.PREF_IS_URL_LIVE, true)
        return if(isLive)
            Configs.BASE_URL_LIVE
        else
            Configs.BASE_URL_DEMO
    }


}