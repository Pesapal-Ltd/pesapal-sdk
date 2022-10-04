package com.pesapal.paygateway.activities.payment.repo

import com.pesapal.paygateway.activities.payment.data.api.ApiClient
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.paygateway.activities.payment.model.auth.AuthRequestModel
import com.pesapal.paygateway.activities.payment.model.auth.AuthResponseModel
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paygateway.activities.payment.model.mobile_money.TransactionStatusResponse
import com.pesapal.paygateway.activities.payment.utils.RetrofitErrorUtil
import com.pesapal.paygateway.activities.payment.utils.Resource
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
                    val error = sendLogs.error.message
                    Resource.error(error!!)
                }
            } catch (e: Exception) {
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }
    }


    suspend fun registerApi(registerIpnRequest: RegisterIpnRequest): Resource<RegisterIpnResponse> {
        return withContext(Dispatchers.IO){
            try{
                val registerIpn = apiService.registerIpn(registerIpnRequest)
                if(registerIpn.status != null && registerIpn.status == "200") {
                    Resource.success(registerIpn)
                }else{
                    val error = registerIpn.error.message
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
                val mobileMoneyCheckout = apiService.mobileMoneyCheckout(mobileMoneyRequest)
                if(mobileMoneyCheckout.status != null && (mobileMoneyCheckout.status == "200" || mobileMoneyCheckout.status =="500")) {
                    Resource.success(mobileMoneyCheckout)
                }else{
                    val error = mobileMoneyCheckout.error?.message
                    Resource.error(error!!)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }


    suspend fun getTransactionStatus(orderTrackingId: String): Resource<TransactionStatusResponse> {
        return withContext(Dispatchers.IO){
            try{
                val transactionStatus = apiService.getTransactionStatus(orderTrackingId)
                val completed = transactionStatus.paymentStatusDescription
                if(transactionStatus.status != null && transactionStatus.status == "200") {
                    if(completed == "Completed") {
                        Resource.success(transactionStatus)
                    }else{
                        Resource.error("Awaiting Payment")
                    }
                }else{
                    val error = transactionStatus.error.message
                    Resource.error(error)
                }
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }



}