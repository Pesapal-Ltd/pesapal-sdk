package com.pesapal.paygateway.activities.payment.data.api

import com.pesapal.paygateway.activities.payment.utils.PrefManager
import com.pesapal.paygateway.activities.payment.Configs
import com.pesapal.paygateway.activities.payment.data.services.ApiServices
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
                    .build() }

        val apiServices: ApiServices get() = retrofit.create(ApiServices::class.java)

        private fun getOkHttpClient(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(interceptor)
                .addInterceptor { chain ->
//                    if(PrefManager.getToken() != null) {
//                        val token =  "Bearer "+PrefManager.getToken()
//                        val newRequest = chain.request().newBuilder()
//                            .addHeader(
//                                "Authorization",
//                                token
//                            )
//                            .build()
//                        chain.proceed(newRequest)
//                    }else{
                        val newRequest = chain.request().newBuilder()
                            .build()
                        chain.proceed(newRequest)
//                    }

                }
            return builder.build()
        }

}