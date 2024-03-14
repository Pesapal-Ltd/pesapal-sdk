package com.pesapal.sdk.fragment.card.repo

import android.util.Log
import com.pesapal.paygateway.activities.payment.model.check3ds.CheckDSecureRequest
import com.pesapal.paygateway.activities.payment.model.check3ds.response.CheckDsResponse
import com.pesapal.paygateway.activities.payment.model.check3ds.token.DsTokenRequest
import com.pesapal.sdk.Sdkapp
import com.pesapal.sdk.data.api.ApiClient
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.model.card.CardinalRequest
import com.pesapal.sdk.model.card.CardinalResponse
import com.pesapal.sdk.model.card.RequestServerJwt
import com.pesapal.sdk.model.card.ResponseServerJwt
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequestRed
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequestRedRoot
import com.pesapal.sdk.model.card.submit.response.SubmitCardResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.PrefManager
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.RetrofitErrorUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class CardRepository {

    private val apiService = ApiClient.apiServices
    suspend fun generateCardOrderTrackingId(cardOrderTrackingIdRequest: CardOrderTrackingIdRequest): Resource<CardOrderTrackingIdResponse> {
        return withContext(Dispatchers.IO){
            try{
                val cardExpressCheckoutResponse = apiService.generateCardOrderTrackingId("Bearer "+ PrefManager.getToken(
                    Sdkapp.getContextInstance()),cardOrderTrackingIdRequest)
//                if(cardExpressCheckoutResponse.status != null && (cardExpressCheckoutResponse.status == "200" || cardExpressCheckoutResponse.status =="500")) {
                if(cardExpressCheckoutResponse.status != null && (cardExpressCheckoutResponse.status == "200" )) {
                    Resource.success(cardExpressCheckoutResponse)
                }else{
                    var error = cardExpressCheckoutResponse.error?.message
                    if(error == ""){
                        error =  cardExpressCheckoutResponse.error?.code
                    }
                    Resource.error(error!!, cardExpressCheckoutResponse)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }



    suspend fun submitCardRequest(processCardRequestV: SubmitCardRequest): Resource<SubmitCardResponse> {
        return withContext(Dispatchers.IO){
            try{
                val cardExpressCheckoutResponse = apiService.submitCardRequest("Bearer "+ PrefManager.getToken(Sdkapp.getContextInstance()),processCardRequestV)
                if(cardExpressCheckoutResponse.status == "200") {
                    Resource.success(cardExpressCheckoutResponse)
                }else{
                    var error = cardExpressCheckoutResponse.error?.message
                    if(error == ""){
                        error =  cardExpressCheckoutResponse.error?.code
                    }
                    Resource.error(error!!, cardExpressCheckoutResponse)
                }
            }catch (e: Exception){
                Log.e("Card" ,"Actual error " + e.localizedMessage)
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }
    suspend fun submitCardRequest(submitCardRequestRedRoot: SubmitCardRequestRedRoot): Resource<SubmitCardResponse> {
        return withContext(Dispatchers.IO){
            try{
                val cardExpressCheckoutResponse = apiService.submitCardRequest("Bearer "+ PrefManager.getToken(Sdkapp.getContextInstance()),submitCardRequestRedRoot)
                if(cardExpressCheckoutResponse.status == "200") {
                    Resource.success(cardExpressCheckoutResponse)
                }else{
                    var error = cardExpressCheckoutResponse.error?.message
                    if(error == ""){
                        error =  cardExpressCheckoutResponse.error?.code
                    }
                    Resource.error(error!!, cardExpressCheckoutResponse)
                }
            }catch (e: Exception){
                Log.e("Card" ,"Actual error " + e.localizedMessage)
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }

    suspend fun getCardTransactionStatus(orderTrackingId: String): Resource<TransactionStatusResponse> {
        return withContext(Dispatchers.IO){
            try{
                val transactionStatus = apiService.checkCardPaymentStatus("Bearer "+ PrefManager.getToken(Sdkapp.getContextInstance()),orderTrackingId)
                if(transactionStatus.status != null && transactionStatus.status == "200") {
                    Resource.success(transactionStatus)
                }else{
                    val error = transactionStatus.error?.message!!
                    Resource.error(error, transactionStatus)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }

    suspend fun getCardinalToken(cardRequest: CardinalRequest): Resource<CardinalResponse> {
        return withContext(Dispatchers.IO){
            try{
                val transactionStatus = apiService.signCardinal("Bearer "+ PrefManager.getToken(Sdkapp.getContextInstance()),cardRequest)
                if(transactionStatus.status != null && transactionStatus.status == "200") {
                    Resource.success(transactionStatus)
                }else{
                    val error = transactionStatus.message!!
                    Resource.error(error)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }


    suspend fun serverJwt(requestServerJwt: RequestServerJwt): Resource<ResponseServerJwt>{

        return withContext(Dispatchers.IO){
            try{
                val serverJwt = apiService.getServerJwt("Bearer "+ PrefManager.getToken(Sdkapp.getContextInstance()),requestServerJwt)
                if(serverJwt.status == "200"){
                    Resource.success(serverJwt)
                }else{
                    val error = serverJwt.message;
                    Resource.error(error)
                }

            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }
    }

    suspend fun dsToken(dsTokenRequest: DsTokenRequest): Resource<AuthResponseModel> {
        return  withContext(Dispatchers.IO) {
            try {
                val sendLogs = apiService.dsToken(dsTokenRequest)
                if(sendLogs.status != null && sendLogs.status == "200") {
                    Resource.success(sendLogs)
                }else{
                    val error = sendLogs.error.message
                    Resource.error(error!!)
                }
            } catch (e: Exception) {
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }
    }

    suspend fun check3ds(checkDSecureRequest: CheckDSecureRequest, token: String): Resource<CheckDsResponse> {
        return  withContext(Dispatchers.IO) {
            try {
                val sendLogs = apiService.check3ds(token,checkDSecureRequest)
//                if(sendLogs != null) {
                Resource.success(sendLogs)
//                }else{
//                    val error = sendLogs.message
//                    Resource.error(error!!)
//                }
            } catch (e: Exception) {
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }
    }


}