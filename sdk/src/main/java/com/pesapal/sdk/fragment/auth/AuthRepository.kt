package com.pesapal.sdk.fragment.auth

import com.pesapal.sdk.data.api.ApiClient
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.RetrofitErrorUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

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


}