package com.pesapal.sdk.fragment.auth

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.sdk.utils.sec.device.integrity.PlayIntegrityResponse
import com.pesapal.sdk.model.accountinfo.AccountInfoRequest
import com.pesapal.sdk.model.accountinfo.AccountInfoResponse
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.sdk.model.txn_status.TransactionError
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.Status
import com.pesapal.sdk.utils.sec.ParseUtil
import com.pesapal.sdk.utils.sec.device.integrity.PlayIntegrityRequest
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec

internal class AuthViewModel : ViewModel() {

    private var authRepository = AuthRepository()

    private var _authResponse = MutableLiveData<Resource<AuthResponseModel>>()
    val authResponse: LiveData<Resource<AuthResponseModel>>
        get() = _authResponse

    private var _registerIpnResponse = MutableLiveData<Resource<RegisterIpnResponse>>()
    val registerIpnResponse: LiveData<Resource<RegisterIpnResponse>>
        get() = _registerIpnResponse


    private var _accountResp = MutableLiveData<Resource<AccountInfoResponse>>()
    val accountResp: LiveData<Resource<AccountInfoResponse>>
        get() = _accountResp

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

    fun retrieveAccountInfo(accountInfoRequest: AccountInfoRequest){
        _accountResp.postValue(Resource.loading("Setting up ... "))
        viewModelScope.launch {
            val result = authRepository.getAccountInfo(accountInfoRequest)
            when(result.status){
                Status.ERROR -> {
                    _accountResp.postValue(Resource.error(result.message!!))
                    _handleError.postValue(Resource.error(result.message))
                }
                Status.SUCCESS -> {
                    _accountResp.postValue(Resource.success(result.data))
                }
                else -> {
                    _handleError.postValue(Resource.error(result.message!!))
                }
            }

        }
    }

//    fun verifyToken(
//        playIntegrityRequest: PlayIntegrityRequest
//    ){
//        _verifyToken.postValue(Resource.loading("Processing request ..."))
//        viewModelScope.launch{
//            val result = splashRepository.verifyToken(playIntegrityRequest)
//            when (result.status) {
//                Status.ERROR -> {
//                    _verifyToken.postValue(Resource.error(result.message!!))
//                }
//                Status.SUCCESS -> {
//                    _verifyToken.postValue(Resource.success(result.data))
//                }
//                else -> {
//                }
//            }
//        }
//    }



}