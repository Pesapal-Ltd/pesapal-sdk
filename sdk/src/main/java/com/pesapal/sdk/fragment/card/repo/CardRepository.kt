package com.pesapal.sdk.fragment.card.repo

import com.pesapal.sdk.data.api.ApiClient
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.model.card.submit.response.SubmitCardResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.PrefManager
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.RetrofitErrorUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardRepository {

    private val apiService = ApiClient.apiServices
    suspend fun generateCardOrderTrackingId(cardOrderTrackingIdRequest: CardOrderTrackingIdRequest): Resource<CardOrderTrackingIdResponse> {
        return withContext(Dispatchers.IO){
            try{
                val cardExpressCheckoutResponse = apiService.generateCardOrderTrackingId("Bearer "+ PrefManager.getToken(),cardOrderTrackingIdRequest)
                if(cardExpressCheckoutResponse.status != null && (cardExpressCheckoutResponse.status == "200" || cardExpressCheckoutResponse.status =="500")) {
                    Resource.success(cardExpressCheckoutResponse)
                }else{
                    var error = cardExpressCheckoutResponse.error?.message
                    if(error == ""){
                        error =  cardExpressCheckoutResponse.error?.code
                    }
                    Resource.error(error!!)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }



    suspend fun submitCardRequest(processCardRequestV: SubmitCardRequest): Resource<SubmitCardResponse> {
        return withContext(Dispatchers.IO){
            try{
                val cardExpressCheckoutResponse = apiService.submitCardRequest("Bearer "+ PrefManager.getToken(),processCardRequestV)
                if(cardExpressCheckoutResponse.status == "200" && cardExpressCheckoutResponse.error == null) {
                    Resource.success(cardExpressCheckoutResponse)
                }else{
                    var error = cardExpressCheckoutResponse.error?.message
                    if(error == ""){
                        error =  cardExpressCheckoutResponse.error?.code
                    }
                    Resource.error(error!!)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }

    suspend fun getCardTransactionStatus(orderTrackingId: String): Resource<TransactionStatusResponse> {
        return withContext(Dispatchers.IO){
            try{
                val transactionStatus = apiService.checkCardPaymentStatus("Bearer "+ PrefManager.getToken(),orderTrackingId)
                if(transactionStatus.status != null && transactionStatus.status == "200") {
                    Resource.success(transactionStatus)
                }else{
                    val error = transactionStatus.error?.message!!
                    Resource.error(error)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }
}