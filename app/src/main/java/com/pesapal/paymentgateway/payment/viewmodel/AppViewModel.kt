package com.pesapal.paymentgateway.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.paymentgateway.payment.model.auth.AuthRequestModel
import com.pesapal.paymentgateway.payment.model.auth.AuthResponseModel
import com.pesapal.paymentgateway.payment.repo.PaymentRepository
import com.pesapal.paymentgateway.payment.utils.Resource
import com.pesapal.paymentgateway.payment.utils.Status
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    private val paymentRepository = PaymentRepository()

    private var _authPaymentResponse = MutableLiveData<Resource<AuthResponseModel>>()
    val authPaymentResponse: LiveData<Resource<AuthResponseModel>>
        get() = _authPaymentResponse

    fun authPayment(authRequestModel: AuthRequestModel) {
        _authPaymentResponse.postValue(Resource.loading("Initiating payment process ... "))
        viewModelScope.launch {
            val result = paymentRepository.authPayment(authRequestModel)
            when (result.status) {
                Status.ERROR -> {
                    _authPaymentResponse.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _authPaymentResponse.postValue(Resource.success(result.data))
                }
                else -> {}
            }
        }

    }


}