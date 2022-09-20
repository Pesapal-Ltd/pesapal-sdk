package com.pesapal.paymentgateway.payment.repo

import com.pesapal.paymentgateway.payment.data.api.ApiClient
import com.pesapal.paymentgateway.payment.model.RegisterIpnUrl.RegisterIpnRequest
import com.pesapal.paymentgateway.payment.model.RegisterIpnUrl.RegisterIpnResponse
import com.pesapal.paymentgateway.payment.model.auth.AuthRequestModel
import com.pesapal.paymentgateway.payment.model.auth.AuthResponseModel
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paymentgateway.payment.utils.Resource
import com.pesapal.paymentgateway.payment.utils.RetrofitErrorUtil
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
                Resource.success(sendLogs)
            } catch (e: Exception) {
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }
    }


    suspend fun registerApi(registerIpnRequest: RegisterIpnRequest):Resource<RegisterIpnResponse>{
        return withContext(Dispatchers.IO){
            try{
                val registerIpn = apiService.registerIpn(registerIpnRequest)
                Resource.success(registerIpn)
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }
    }

    suspend fun mobileMoneyApi(mobileMoneyRequest: MobileMoneyRequest):Resource<MobileMoneyResponse>{
        return withContext(Dispatchers.IO){
            try{
                val registerIpn = apiService.mobileMoneyCheckout(mobileMoneyRequest)
                Resource.success(registerIpn)
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }


    suspend fun getTransactionStatus(orderTrackingId: String):Resource<MobileMoneyResponse>{
        return withContext(Dispatchers.IO){
            try{
                val registerIpn = apiService.getTransactionStatus(orderTrackingId)
                Resource.success(registerIpn)
            }catch (e: Exception){
                Resource.error(RetrofitErrorUtil.serverException(e))
            }
        }

    }



}