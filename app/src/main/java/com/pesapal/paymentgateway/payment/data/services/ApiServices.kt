package com.pesapal.paymentgateway.payment.data.services

import com.pesapal.paymentgateway.payment.model.RegisterIpnUrl.RegisterIpnRequest
import com.pesapal.paymentgateway.payment.model.RegisterIpnUrl.RegisterIpnResponse
import com.pesapal.paymentgateway.payment.model.auth.AuthRequestModel
import com.pesapal.paymentgateway.payment.model.auth.AuthResponseModel
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiServices {

    @POST("api/Auth/RequestToken")
    suspend fun authPayment(@Body authRequestModel: AuthRequestModel) : AuthResponseModel

    @POST("api/URLSetup/RegisterIPN")
    suspend fun registerIpn(@Body registerIpnRequest: RegisterIpnRequest) : RegisterIpnResponse


}