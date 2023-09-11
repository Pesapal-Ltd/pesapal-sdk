package com.pesapal.sdk.fragment.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.sdk.model.txn_status.TransactionError
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.Status
import kotlinx.coroutines.launch

internal class AuthViewModel : ViewModel() {

    private var authRepository = AuthRepository()

    private var _authResponse = MutableLiveData<Resource<AuthResponseModel>>()
    val authResponse: LiveData<Resource<AuthResponseModel>>
        get() = _authResponse

    private var _registerIpnResponse = MutableLiveData<Resource<RegisterIpnResponse>>()
    val registerIpnResponse: LiveData<Resource<RegisterIpnResponse>>
        get() = _registerIpnResponse

    private var _handleError = MutableLiveData<Resource<TransactionError>>()
    val handleError: LiveData<Resource<TransactionError>>
        get() = _handleError


    fun authPayment(authRequestModel: AuthRequestModel) {
        _authResponse.postValue(Resource.loading("Initiating payment process ... "))
        viewModelScope.launch {
            val result = authRepository.authPayment(authRequestModel)
            when (result.status) {
                Status.SUCCESS -> {
                    _authResponse.postValue(Resource.success(result.data))
                }
                Status.ERROR -> {
                    _authResponse.postValue(Resource.error(result.message!!))
                }
                else -> {
                    _authResponse.postValue(Resource.error(result.message!!))
                }
            }
        }
    }

    fun registerIpn(registerIpnRequest: RegisterIpnRequest){
        _registerIpnResponse.postValue(Resource.loading("Registering Ipn ... "))
        viewModelScope.launch {
            val result = authRepository.registerApi(registerIpnRequest)
            when(result.status){
                Status.ERROR -> {
                    _registerIpnResponse.postValue(Resource.error(result.message!!))
                    _handleError.postValue(Resource.error(result.message))
                }
                Status.SUCCESS -> {
                    _registerIpnResponse.postValue(Resource.success(result.data))
                }
                else -> {
                    _handleError.postValue(Resource.error(result.message!!))
                }
            }

        }
    }


}