package com.pesapal.sdk.fragment.mobile_money.mpesa.repo

import com.pesapal.sdk.Sdkapp
import com.pesapal.sdk.data.api.ApiClient
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.PrefManager
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.RetrofitErrorUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class MpesaRepository {
    private val apiService = ApiClient.apiServices


    suspend fun mobileMoneyApi(mobileMoneyRequest: MobileMoneyRequest): Resource<MobileMoneyResponse> {
        return withContext(Dispatchers.IO){
            try{
                val mobileMoneyCheckout = apiService.submitMobileMoneyCheckout("Bearer "+ PrefManager.getToken(
                    Sdkapp.getInstance()),mobileMoneyRequest)
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



    suspend fun getTransactionStatus(orderTrackingId: String): Resource<TransactionStatusResponse> {
        return withContext(Dispatchers.IO){
            try{
                val transactionStatus = apiService.getTransactionStatus("Bearer "+ PrefManager.getToken(Sdkapp.getInstance()),orderTrackingId)
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