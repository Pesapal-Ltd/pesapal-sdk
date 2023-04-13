package com.pesapal.sdk.repo

import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.model.card.submit.response.SubmitCardResponse
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentRepository {

    private val apiService = com.pesapal.sdk.data.api.ApiClient.apiServices

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
                Resource.error(com.pesapal.sdk.utils.RetrofitErrorUtil.serverException(e))
            }
        }
    }

    suspend fun registerApi(registerIpnRequest: RegisterIpnRequest): Resource<RegisterIpnResponse> {
        return withContext(Dispatchers.IO){
            try{
                val registerIpn = apiService.registerIpn("Bearer "+ com.pesapal.sdk.utils.PrefManager.getToken(),registerIpnRequest)
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
                Resource.error(com.pesapal.sdk.utils.RetrofitErrorUtil.serverException(e))
            }
        }
    }

    suspend fun mobileMoneyApi(mobileMoneyRequest: MobileMoneyRequest): Resource<MobileMoneyResponse> {
        return withContext(Dispatchers.IO){
            try{
                val mobileMoneyCheckout = apiService.submitMobileMoneyCheckout("Bearer "+ com.pesapal.sdk.utils.PrefManager.getToken(),mobileMoneyRequest)
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
                Resource.error(com.pesapal.sdk.utils.RetrofitErrorUtil.serverException(e))
            }
        }

    }


    suspend fun generateCardOrderTrackingId(cardOrderTrackingIdRequest: CardOrderTrackingIdRequest): Resource<CardOrderTrackingIdResponse> {
        return withContext(Dispatchers.IO){
            try{
                val cardExpressCheckoutResponse = apiService.generateCardOrderTrackingId("Bearer "+ com.pesapal.sdk.utils.PrefManager.getToken(),cardOrderTrackingIdRequest)
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
                Resource.error(com.pesapal.sdk.utils.RetrofitErrorUtil.serverException(e))
            }
        }

    }



    suspend fun submitCardRequest(processCardRequestV: SubmitCardRequest): Resource<SubmitCardResponse> {
        return withContext(Dispatchers.IO){
            try{
                val cardExpressCheckoutResponse = apiService.submitCardRequest("Bearer "+ com.pesapal.sdk.utils.PrefManager.getToken(),processCardRequestV)
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
                Resource.error(com.pesapal.sdk.utils.RetrofitErrorUtil.serverException(e))
            }
        }

    }

    suspend fun getCardTransactionStatus(orderTrackingId: String): Resource<TransactionStatusResponse> {
        return withContext(Dispatchers.IO){
            try{
                val transactionStatus = apiService.checkCardPaymentStatus("Bearer "+ com.pesapal.sdk.utils.PrefManager.getToken(),orderTrackingId)
                if(transactionStatus.status != null && transactionStatus.status == "200") {
                        Resource.success(transactionStatus)
                }else{
                    val error = transactionStatus.error?.message!!
                    Resource.error(error)
                }
            }catch (e: Exception){
                Resource.error(com.pesapal.sdk.utils.RetrofitErrorUtil.serverException(e))
            }
        }

    }


    suspend fun getTransactionStatus(orderTrackingId: String): Resource<TransactionStatusResponse> {
        return withContext(Dispatchers.IO){
            try{
                val transactionStatus = apiService.getTransactionStatus("Bearer "+ com.pesapal.sdk.utils.PrefManager.getToken(),orderTrackingId)
                if(transactionStatus.status != null && (transactionStatus.status == "200" || transactionStatus.status == "500")) {
                        Resource.success(transactionStatus)
                }else{
                    val error = transactionStatus.error?.message!!
                    Resource.error(error)
                }
            }catch (e: Exception){
                Resource.error(com.pesapal.sdk.utils.RetrofitErrorUtil.serverException(e))
            }
        }

    }


}