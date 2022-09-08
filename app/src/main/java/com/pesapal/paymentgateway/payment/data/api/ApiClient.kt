package com.pesapal.paymentgateway.payment.data.api

import com.pesapal.paymentgateway.payment.Configs
import com.pesapal.paymentgateway.payment.data.services.ApiServices
import com.pesapal.paymentgateway.payment.utils.PrefManager
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
                    if(PrefManager.getToken() != null) {
                        val token =  "Bearer "+PrefManager.getToken()
                        val newRequest = chain.request().newBuilder()
                            .addHeader(
                                "Authorization",
                                token
                            )
                            .build()
                        chain.proceed(newRequest)
                    }else{
                        val newRequest = chain.request().newBuilder()
                            .build()
                        chain.proceed(newRequest)
                    }

                }
            return builder.build()
        }

}