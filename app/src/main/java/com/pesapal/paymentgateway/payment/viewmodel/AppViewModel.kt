package com.pesapal.paymentgateway.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.paymentgateway.payment.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.paymentgateway.payment.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.paymentgateway.payment.model.auth.AuthRequestModel
import com.pesapal.paymentgateway.payment.model.auth.AuthResponseModel
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paymentgateway.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paymentgateway.payment.model.mobile_money.TransactionStatusResponse
import com.pesapal.paymentgateway.payment.repo.PaymentRepository
import com.pesapal.paymentgateway.payment.utils.Resource
import com.pesapal.paymentgateway.payment.utils.Status
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    private val paymentRepository = PaymentRepository()

    private var _authPaymentResponse = MutableLiveData<Resource<AuthResponseModel>>()
    val authPaymentResponse: LiveData<Resource<AuthResponseModel>>
        get() = _authPaymentResponse

    private var _registerIpnResponse = MutableLiveData<Resource<RegisterIpnResponse>>()
    val registerIpnResponse: LiveData<Resource<RegisterIpnResponse>>
        get() = _registerIpnResponse

    private var _mobileMoneyResponse = MutableLiveData<Resource<MobileMoneyResponse>>()
    val mobileMoneyResponse: LiveData<Resource<MobileMoneyResponse>>
        get() = _mobileMoneyResponse

    private var _transactionStatus = MutableLiveData<Resource<TransactionStatusResponse>>()
    val transactionStatus: LiveData<Resource<TransactionStatusResponse>>
        get() = _transactionStatus


    private var _loadFragment = MutableLiveData<Resource<String>>()
    val loadFragment: LiveData<Resource<String>>
    get() = _loadFragment

    private var _loadPendingMpesa = MutableLiveData<Resource<MobileMoneyRequest>>()
    val loadPendingMpesa: LiveData<Resource<MobileMoneyRequest>>
    get() = _loadPendingMpesa

    private var _loadSuccessMpesa = MutableLiveData<Resource<TransactionStatusResponse>>()
    val loadSuccessMpesa: LiveData<Resource<TransactionStatusResponse>>
    get() = _loadSuccessMpesa

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

    fun registerIpn(registerIpnRequest: RegisterIpnRequest){
        _registerIpnResponse.postValue(Resource.loading("Registering Ipn ... "))
        viewModelScope.launch {
            val result = paymentRepository.registerApi(registerIpnRequest)
            when(result.status){
                Status.ERROR -> {
                    _registerIpnResponse.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _registerIpnResponse.postValue(Resource.success(result.data))
                }
                else -> {}
            }

        }
    }

    fun sendMobileMoneyCheckOut(mobileMoneyRequest: MobileMoneyRequest, action: String){
        _mobileMoneyResponse.postValue(Resource.loading(action))
        viewModelScope.launch {
            val result = paymentRepository.mobileMoneyApi(mobileMoneyRequest)
            when(result.status){
                Status.ERROR -> {
                    _mobileMoneyResponse.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _mobileMoneyResponse.postValue(Resource.success(result.data))
                }
                else -> {}
            }

        }
    }

    fun mobileMoneyTransactionStatus(trackingId: String){
        _transactionStatus.postValue(Resource.loading("Confirming payment ... "))
        viewModelScope.launch {
            val result = paymentRepository.getTransactionStatus(trackingId)
            when(result.status){
                Status.ERROR -> {
                    _transactionStatus.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _transactionStatus.postValue(Resource.success(result.data))
                }
                else -> {}
            }

        }
    }


    fun showPendingMpesaPayment(mobileMoneyRequest: MobileMoneyRequest){
        _loadPendingMpesa.postValue(Resource.success(mobileMoneyRequest))
    }


    fun showSuccessMpesaPayment(transactionStatusResponse: TransactionStatusResponse){
        _loadSuccessMpesa.postValue(Resource.success(transactionStatusResponse))
    }


    fun loadFragment(frag: String){
        _loadFragment.postValue(Resource.loadFragment(frag))
    }






}