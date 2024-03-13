package com.pesapal.sdk.data.services

import com.pesapal.paygateway.activities.payment.model.check3ds.CheckDSecureRequest
import com.pesapal.paygateway.activities.payment.model.check3ds.response.CheckDsResponse
import com.pesapal.paygateway.activities.payment.model.check3ds.token.DsTokenRequest
import com.pesapal.sdk.model.accountinfo.AccountInfoRequest
import com.pesapal.sdk.model.accountinfo.AccountInfoResponse
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.model.card.CardinalRequest
import com.pesapal.sdk.model.card.CardinalResponse
import com.pesapal.sdk.model.card.RequestServerJwt
import com.pesapal.sdk.model.card.ResponseServerJwt
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.model.card.submit.response.SubmitCardResponse
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnResponse
import retrofit2.http.*

internal interface ApiServices {

    @POST("api/Auth/RequestToken")
    suspend fun authPayment(@Body authRequestModel: AuthRequestModel) : AuthResponseModel

    @POST("api/URLSetup/RegisterIPN")
    suspend fun registerIpn(@Header("Authorization") token: String, @Body registerIpnRequest: RegisterIpnRequest) : RegisterIpnResponse

    @POST("api/transactions/expresscheckout")
    suspend fun submitMobileMoneyCheckout(@Header("Authorization") token: String, @Body mobileMoneyRequest: MobileMoneyRequest) : MobileMoneyResponse

    @POST("api/transactions/expresscheckout")
    suspend fun generateCardOrderTrackingId(@Header("Authorization") token: String, @Body cardOrderTrackingIdRequest: CardOrderTrackingIdRequest) : CardOrderTrackingIdResponse

    @POST("api/transactions/expresscardrequest")
    suspend fun submitCardRequest(@Header("Authorization") token: String, @Body submitCardRequest: SubmitCardRequest) : SubmitCardResponse

    @GET("api/Transactions/GetTransactionStatus")
    suspend fun checkCardPaymentStatus(@Header("Authorization") token: String, @Query("orderTrackingId") orderTrackingId: String) : TransactionStatusResponse

    @GET("api/Transactions/GetTransactionStatus")
    suspend fun getTransactionStatus(@Header("Authorization") token: String, @Query("orderTrackingId") orderTrackingId: String) : TransactionStatusResponse



    @Deprecated("OLDER VERSION")
    @GET("api/Transactions/SignCardinalCheckoutRequest")
    suspend fun signCardinal(@Header("Authorization") token: String, @Body cardinalRequest: CardinalRequest) : CardinalResponse


    @POST("api/Transactions/SignCardinalCheckoutRequest")
    suspend fun getServerJwt(@Header("Authorization") token: String, @Body requestServerJwt: RequestServerJwt) : ResponseServerJwt

    @POST("https://cybqa.pesapal.com/pesapalcharging/api/Token/RequestApiToken")
    suspend fun dsToken( @Body dsTokenRequest: DsTokenRequest) : AuthResponseModel


    // TODO: https://cybqa.pesapal.com/pesapalv3 has a different base url to https://cybqa.pesapal.com/pesapalcharging
    // todo: Base URL to change on one side
    @POST("https://cybqa.pesapal.com/pesapalcharging/api/Transaction/CheckEnrollMent")
    suspend fun check3ds(@Header("Authorization") token: String, @Body checkDSecureRequest: CheckDSecureRequest) : CheckDsResponse

    @POST("api/Transactions/AccountInfo")
    suspend fun getAccountInfo(@Body accountInfoRequest: AccountInfoRequest) : AccountInfoResponse


}