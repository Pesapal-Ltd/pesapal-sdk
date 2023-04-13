package com.pesapal.sdk.fragment.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.Status
import kotlinx.coroutines.launch
class AuthViewModel : ViewModel() {

    private var authRepository = AuthRepository()

    private var _authResponse = MutableLiveData<Resource<AuthResponseModel>>()
    val authResponse: LiveData<Resource<AuthResponseModel>>
        get() = _authResponse

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

}