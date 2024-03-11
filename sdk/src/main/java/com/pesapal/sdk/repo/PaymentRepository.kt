package com.pesapal.sdk.repo

import com.pesapal.sdk.Sdkapp
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PaymentRepository {

    private val apiService = com.pesapal.sdk.data.api.ApiClient.apiServices

    suspend fun registerApi(registerIpnRequest: RegisterIpnRequest): Resource<RegisterIpnResponse> {
        return withContext(Dispatchers.IO){
            try{
                val registerIpn = apiService.registerIpn("Bearer "+ com.pesapal.sdk.utils.PrefManager.getToken(Sdkapp.getInstance()),registerIpnRequest)
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
                val mobileMoneyCheckout = apiService.submitMobileMoneyCheckout("Bearer "+ com.pesapal.sdk.utils.PrefManager.getToken(Sdkapp.getInstance()),mobileMoneyRequest)
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



    suspend fun getTransactionStatus(orderTrackingId: String): Resource<TransactionStatusResponse> {
        return withContext(Dispatchers.IO){
            try{
                val transactionStatus = apiService.getTransactionStatus("Bearer "+ com.pesapal.sdk.utils.PrefManager.getToken(Sdkapp.getInstance()),orderTrackingId)
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