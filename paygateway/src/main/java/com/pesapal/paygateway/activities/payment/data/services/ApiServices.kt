package com.pesapal.paygateway.activities.payment.data.services

import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.paygateway.activities.payment.model.auth.AuthRequestModel
import com.pesapal.paygateway.activities.payment.model.auth.AuthResponseModel
import com.pesapal.paygateway.activities.payment.model.fx.ForexChange
import com.pesapal.paygateway.activities.payment.model.fx.ForexExchangeResponse
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paygateway.activities.payment.model.mobile_money.TransactionStatusResponse
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

    @POST("api/fx/quote")
    suspend fun getFxRate(@Body forexChange: ForexChange) : ForexExchangeResponse


}