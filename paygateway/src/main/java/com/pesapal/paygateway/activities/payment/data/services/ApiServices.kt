package com.pesapal.paygateway.activities.payment.data.services

import com.pesapal.paygateway.activities.payment.model.auth.AuthRequestModel
import com.pesapal.paygateway.activities.payment.model.auth.AuthResponseModel
import com.pesapal.paygateway.activities.payment.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.paygateway.activities.payment.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.paygateway.activities.payment.model.card.submit.request.SubmitCardRequest
import com.pesapal.paygateway.activities.payment.model.card.submit.response.SubmitCardResponse
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paygateway.activities.payment.model.txn_status.TransactionStatusResponse
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnResponse
import retrofit2.http.*

interface ApiServices {

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

}