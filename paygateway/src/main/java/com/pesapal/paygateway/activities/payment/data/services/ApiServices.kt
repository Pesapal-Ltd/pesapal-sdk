package com.pesapal.paygateway.activities.payment.data.services

import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.paygateway.activities.payment.model.auth.AuthRequestModel
import com.pesapal.paygateway.activities.payment.model.auth.AuthResponseModel
import com.pesapal.paygateway.activities.payment.model.check3ds.CheckDSecureRequest
import com.pesapal.paygateway.activities.payment.model.check3ds.response.CheckDsResponse
import com.pesapal.paygateway.activities.payment.model.check3ds.token.DsTokenRequest
import com.pesapal.paygateway.activities.payment.model.fx.ForexChange
import com.pesapal.paygateway.activities.payment.model.fx.ForexExchangeResponse
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paygateway.activities.payment.model.mobile_money.TransactionStatusResponse
import com.pesapal.paygateway.activities.payment.model.server_jwt.RequestServerJwt
import com.pesapal.paygateway.activities.payment.model.server_jwt.response.ResponseServerJwt
import retrofit2.http.*


interface ApiServices {

    @POST("api/Auth/RequestToken")
    suspend fun authPayment(@Body authRequestModel: AuthRequestModel) : AuthResponseModel

    @POST("https://cybqa.pesapal.com/pesapalcharging/api/Token/RequestApiToken")
    suspend fun dsToken( @Body dsTokenRequest: DsTokenRequest) : AuthResponseModel

    @POST("https://cybqa.pesapal.com/pesapalcharging/api/Transaction/CheckEnrollMent")
    suspend fun check3ds(@Header("Authorization") token: String, @Body checkDSecureRequest: CheckDSecureRequest) : CheckDsResponse

    @POST("api/URLSetup/RegisterIPN")
    suspend fun registerIpn(@Header("Authorization") token: String, @Body registerIpnRequest: RegisterIpnRequest) : RegisterIpnResponse


    @POST("api/transactions/expresscheckout")
    suspend fun mobileMoneyCheckout(@Header("Authorization") token: String, @Body mobileMoneyRequest: MobileMoneyRequest) : MobileMoneyResponse


    @GET("api/Transactions/GetTransactionStatus")
    suspend fun getTransactionStatus(@Header("Authorization") token: String, @Query("orderTrackingId") orderTrackingId: String) : TransactionStatusResponse

    @POST("api/fx/quote")
    suspend fun getFxRate(@Body forexChange: ForexChange) : ForexExchangeResponse

    @POST("api/Transactions/SignCardinalCheckoutRequest")
    suspend fun getServerJwt(@Header("Authorization") token: String, @Body requestServerJwt: RequestServerJwt) : ResponseServerJwt



}