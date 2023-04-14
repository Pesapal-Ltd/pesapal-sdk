package com.pesapal.sdk.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.sdk.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.sdk.model.auth.AuthRequestModel
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.sdk.model.card.submit.response.SubmitCardResponse
import com.pesapal.sdk.model.txn_status.TransactionError
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.repo.PaymentRepository
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.Status
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    private val paymentRepository = PaymentRepository()

    private var _authPaymentResponse = MutableLiveData<Resource<AuthResponseModel>>()
    val authPaymentResponse: LiveData<Resource<AuthResponseModel>>
        get() = _authPaymentResponse


    private var _registerIpnResponse = MutableLiveData<Resource<RegisterIpnResponse>>()
    val registerIpnResponse: LiveData<Resource<RegisterIpnResponse>>
        get() = _registerIpnResponse

    private var _completeCardPayment = MutableLiveData<Resource<TransactionStatusResponse>>()
    val completeCardPayment: LiveData<Resource<TransactionStatusResponse>>
        get() = _completeCardPayment


    private var _handleError = MutableLiveData<Resource<TransactionError>>()
    val handleError: LiveData<Resource<TransactionError>>
        get() = _handleError


    private var _mobileMoneyResponse = MutableLiveData<Resource<MobileMoneyResponse>>()
    val mobileMoneyResponse: LiveData<Resource<MobileMoneyResponse>>
        get() = _mobileMoneyResponse

    private var _transactionStatus = MutableLiveData<Resource<TransactionStatusResponse>>()
    val transactionStatus: LiveData<Resource<TransactionStatusResponse>>
        get() = _transactionStatus

    private var _transactionStatusBg = MutableLiveData<Resource<TransactionStatusResponse>>()
    val transactionStatusBg: LiveData<Resource<TransactionStatusResponse>>
        get() = _transactionStatusBg

    private var _paymentDone = MutableLiveData<Resource<String>>()
    val paymentDone: LiveData<Resource<String>>
        get() = _paymentDone

    private var _loadFragment = MutableLiveData<Resource<String>>()
    val loadFragment: LiveData<Resource<String>>
    get() = _loadFragment

    private var _loadCardDetails = MutableLiveData<Resource<BillingAddress>>()
    val loadCardDetails: LiveData<Resource<BillingAddress>>
    get() = _loadCardDetails

    private var _loadPendingMpesa = MutableLiveData<Resource<MobileMoneyRequest>>()
    val loadPendingMpesa: LiveData<Resource<MobileMoneyRequest>>
    get() = _loadPendingMpesa


    private var _loadSuccessMpesa = MutableLiveData<Resource<TransactionStatusResponse>>()
    val loadSuccessMpesa: LiveData<Resource<TransactionStatusResponse>>
    get() = _loadSuccessMpesa


    fun registerIpn(registerIpnRequest: RegisterIpnRequest){
        _registerIpnResponse.postValue(Resource.loading("Registering Ipn ... "))
        viewModelScope.launch {
            val result = paymentRepository.registerApi(registerIpnRequest)
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


    fun sendMobileMoneyCheckOut(mobileMoneyRequest: MobileMoneyRequest, action: String){
        _mobileMoneyResponse.postValue(Resource.loading(action))
        viewModelScope.launch {
            val result = paymentRepository.mobileMoneyApi(mobileMoneyRequest)
            when(result.status){
                Status.ERROR -> {
                    _mobileMoneyResponse.postValue(Resource.error(result.message!!))
                    _handleError.postValue(Resource.error(result.message))
                }
                Status.SUCCESS -> {
                    _mobileMoneyResponse.postValue(Resource.success(result.data))
                }
                else -> {
                    _handleError.postValue(Resource.error(result.message!!))
                }
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
                    _handleError.postValue(Resource.error(result.message))
                }
                Status.SUCCESS -> {
                    if(result.data!!.paymentStatusDescription == "Completed") {
                        _transactionStatus.postValue(Resource.success(result.data))
                    }else{
                        _transactionStatus.postValue(Resource.error("Awaiting payment .."))
                    }
                }
                else -> {
                    _handleError.postValue(Resource.error(result.message!!))
                }
            }

        }
    }


    fun mobileMoneyTransactionStatusBackground(trackingId: String){
        _transactionStatusBg.postValue(Resource.loading("Confirming payment ... "))
        viewModelScope.launch {
            val result = paymentRepository.getTransactionStatus(trackingId)
            when(result.status){
                Status.ERROR -> {
                    _transactionStatusBg.postValue(Resource.error(result.message!!))
                    _handleError.postValue(Resource.error(result.message))
                }
                Status.SUCCESS -> {
                    if(result.data!!.paymentStatusDescription == "Completed") {
                        _transactionStatusBg.postValue(Resource.success(result.data))
                    }else{
                        _transactionStatusBg.postValue(Resource.error("Awaiting payment .."))
                    }
                }
                else -> {
                    _handleError.postValue(Resource.error(result.message!!))
                }
            }

        }
    }


    fun handlePaymentStatus(status: String){
        _paymentDone.postValue(Resource.success(status))
    }


    fun showPendingMpesaPayment(mobileMoneyRequest: MobileMoneyRequest){
        _loadPendingMpesa.postValue(Resource.success(mobileMoneyRequest))
    }



    fun showSuccessMpesaPayment(transactionStatusResponse: TransactionStatusResponse){
        _loadSuccessMpesa.postValue(Resource.success(transactionStatusResponse))
    }


    fun loadFragment(frag: String){
        _loadFragment.postValue(Resource.loadFragment(frag) )
    }

    fun loadFragmentV1(billingAddress: BillingAddress){
            _loadCardDetails.postValue(Resource.loadFragment(billingAddress) )
    }


}