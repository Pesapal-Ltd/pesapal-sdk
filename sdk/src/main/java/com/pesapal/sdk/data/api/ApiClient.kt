package com.pesapal.sdk.data.api

import com.pesapal.sdk.BuildConfig
import com.pesapal.sdk.Configs
import com.pesapal.sdk.data.services.ApiServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

        private val retrofit: Retrofit
            get() {
                return Retrofit.Builder()
                    .baseUrl(Configs.BASE_URL)
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

}