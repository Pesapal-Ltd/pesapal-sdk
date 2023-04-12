package com.pesapal.sdk.activities.payment.repo

import com.pesapal.sdk.activities.payment.data.api.ApiClient
import com.pesapal.sdk.activities.payment.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.activities.payment.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.sdk.activities.payment.model.auth.AuthRequestModel
import com.pesapal.sdk.activities.payment.model.auth.AuthResponseModel
import com.pesapal.sdk.activities.payment.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.activities.payment.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.sdk.activities.payment.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.activities.payment.model.card.submit.response.SubmitCardResponse
import com.pesapal.sdk.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.activities.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.activities.payment.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.activities.payment.utils.PrefManager
import com.pesapal.sdk.activities.payment.utils.RetrofitErrorUtil
import com.pesapal.sdk.activities.payment.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentRepository {

    private val apiService = ApiClient.apiServices

    suspend fun authPayment(
        authRequestModel: AuthRequestModel
    ): Resource<AuthResponseModel> {
        return  withContext(Dispatchers.IO) {
            try {
                val sendLogs = apiService.authPayment(authRequestModel)
                if(sendLogs.status != null && sendLogs.status == "200") {
                    Resource.success(sendLogs)
                }else{
                    var error = sendLogs.error.message
                    if(error == ""){
                        error =  sendLogs.error.code
                    }
                    Resource.error(error)
                }
            } catch (e: Exception) {
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }
    }

    suspend fun registerApi(registerIpnRequest: RegisterIpnRequest): Resource<RegisterIpnResponse> {
        return withContext(Dispatchers.IO){
            try{
                val registerIpn = apiService.registerIpn("Bearer "+ PrefManager.getToken(),registerIpnRequest)
                if(registerIpn.status != null && registerIpn.status == "200") {
                    Resource.success(registerIpn)
                }else{
                    var error = registerIpn.error.message
                    if(error == ""){
                        error =  registerIpn.error.code
                    }
                    Resource.error(error!!)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }
    }

    suspend fun mobileMoneyApi(mobileMoneyRequest: MobileMoneyRequest): Resource<MobileMoneyResponse> {
        return withContext(Dispatchers.IO){
            try{
                val mobileMoneyCheckout = apiService.submitMobileMoneyCheckout("Bearer "+ PrefManager.getToken(),mobileMoneyRequest)
                if(mobileMoneyCheckout.status != null && (mobileMoneyCheckout.status == "200" || mobileMoneyCheckout.status =="500")) {
                    Resource.success(mobileMoneyCheckout)
                }else{
                    var error = mobileMoneyCheckout.error?.message
                    if(error == ""){
                        error =  mobileMoneyCheckout.error?.code
                    }
                    Resource.error(error!!)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }


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


    suspend fun getTransactionStatus(orderTrackingId: String): Resource<TransactionStatusResponse> {
        return withContext(Dispatchers.IO){
            try{
                val transactionStatus = apiService.getTransactionStatus("Bearer "+ PrefManager.getToken(),orderTrackingId)
                if(transactionStatus.status != null && (transactionStatus.status == "200" || transactionStatus.status == "500")) {
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