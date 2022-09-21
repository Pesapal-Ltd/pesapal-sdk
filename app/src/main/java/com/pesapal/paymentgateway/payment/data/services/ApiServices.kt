package com.pesapal.paymentgateway.payment.data.services

import com.pesapal.paymentgateway.payment.model.RegisterIpnUrl.RegisterIpnRequest
import com.pesapal.paymentgateway.payment.model.RegisterIpnUrl.RegisterIpnResponse
import com.pesapal.paymentgateway.payment.model.auth.AuthRequestModel
import com.pesapal.paymentgateway.payment.model.auth.AuthResponseModel
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paymentgateway.payment.model.mobile_money.TransactionStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiServices {

    @POST("api/Auth/RequestToken")
    suspend fun authPayment(@Body authRequestModel: AuthRequestModel) : AuthResponseModel

    @POST("api/URLSetup/RegisterIPN")
    suspend fun registerIpn(@Body registerIpnRequest: RegisterIpnRequest) : RegisterIpnResponse

    @POST("api/transactions/expresscheckout")
    suspend fun mobileMoneyCheckout(@Body mobileMoneyRequest: MobileMoneyRequest) : MobileMoneyResponse

    @GET("api/Transactions/GetTransactionStatus")
    suspend fun getTransactionStatus(@Query("orderTrackingId") orderTrackingId: String) : TransactionStatusResponse


}