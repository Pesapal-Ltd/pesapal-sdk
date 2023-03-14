package com.pesapal.paygateway.activities.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnRequest
import com.pesapal.paygateway.activities.payment.model.registerIpn_url.RegisterIpnResponse
import com.pesapal.paygateway.activities.payment.model.auth.AuthRequestModel
import com.pesapal.paygateway.activities.payment.model.auth.AuthResponseModel
import com.pesapal.paygateway.activities.payment.model.card.submit.request.SubmitCardRequest
import com.pesapal.paygateway.activities.payment.model.check3ds.CheckDSecureRequest
import com.pesapal.paygateway.activities.payment.model.check3ds.response.CheckDsResponse
import com.pesapal.paygateway.activities.payment.model.check3ds.token.DsTokenRequest
import com.pesapal.paygateway.activities.payment.model.card.BillingAddress
import com.pesapal.paygateway.activities.payment.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.paygateway.activities.payment.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.paygateway.activities.payment.model.card.status.response.CheckCardPaymentStatusResponse
import com.pesapal.paygateway.activities.payment.model.card.submit.response.SubmitCardResponse
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyRequest
import com.pesapal.paygateway.activities.payment.model.mobile_money.MobileMoneyResponse
import com.pesapal.paygateway.activities.payment.model.mobile_money.TransactionStatusResponse
import com.pesapal.paygateway.activities.payment.model.server_jwt.RequestServerJwt
import com.pesapal.paygateway.activities.payment.model.server_jwt.response.ResponseServerJwt
import com.pesapal.paygateway.activities.payment.repo.PaymentRepository
import com.pesapal.paygateway.activities.payment.utils.Resource
import com.pesapal.paygateway.activities.payment.utils.Status
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    private val paymentRepository = PaymentRepository()

    private var _authPaymentResponse = MutableLiveData<Resource<AuthResponseModel>>()
    val authPaymentResponse: LiveData<Resource<AuthResponseModel>>
        get() = _authPaymentResponse

    private var _dsToken = MutableLiveData<Resource<AuthResponseModel>>()
    val dsToken: LiveData<Resource<AuthResponseModel>>
        get() = _dsToken

    private var _dsResponse = MutableLiveData<Resource<CheckDsResponse>>()
    val dsResponse: LiveData<Resource<CheckDsResponse>>
        get() = _dsResponse

    private var _registerIpnResponse = MutableLiveData<Resource<RegisterIpnResponse>>()
    val registerIpnResponse: LiveData<Resource<RegisterIpnResponse>>
        get() = _registerIpnResponse


    private var _cardOrderTrackingIdResponse = MutableLiveData<Resource<CardOrderTrackingIdResponse>>()
    val cardOrderTrackingIdResponse: LiveData<Resource<CardOrderTrackingIdResponse>>
        get() = _cardOrderTrackingIdResponse

    private var _submitCardResponse = MutableLiveData<Resource<SubmitCardResponse>>()
    val submitCardResponse: LiveData<Resource<SubmitCardResponse>>
        get() = _submitCardResponse


    private var _cardPaymentStatus = MutableLiveData<Resource<TransactionStatusResponse>>()
    val cardPaymentStatus: LiveData<Resource<TransactionStatusResponse>>
        get() = _cardPaymentStatus



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

    private var _serverJwt = MutableLiveData<Resource<ResponseServerJwt>>()
    val serverJwt: LiveData<Resource<ResponseServerJwt>>
    get() = _serverJwt

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
                else -> {
                    _authPaymentResponse.postValue(Resource.error(result.message!!))
                }
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


    fun generateCardOrderTrackingId(cardOrderTrackingIdRequest: CardOrderTrackingIdRequest, action: String){
        _cardOrderTrackingIdResponse.postValue(Resource.loading(action))
        viewModelScope.launch {
            val result = paymentRepository.generateCardOrderTrackingId(cardOrderTrackingIdRequest)
            when(result.status){
                Status.ERROR -> {
                    _cardOrderTrackingIdResponse.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _cardOrderTrackingIdResponse.postValue(Resource.success(result.data))
                }
                else -> {}
            }

        }
    }

    fun submitCardRequest(submitCardRequest: SubmitCardRequest){
        _submitCardResponse.postValue(Resource.loading("Processing request"))
        viewModelScope.launch {
            val result = paymentRepository.submitCardRequest(submitCardRequest)
            when(result.status){
                Status.ERROR -> {
                    _submitCardResponse.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _submitCardResponse.postValue(Resource.success(result.data))
                }
                else -> {}
            }

        }
    }


    fun checkCardPaymentStatus(trackingId: String){
        _cardPaymentStatus.postValue(Resource.loading("Confirming payment ... "))
        viewModelScope.launch {
            val result = paymentRepository.getTransactionStatus(trackingId)
            when(result.status){
                Status.ERROR -> {
                    _cardPaymentStatus.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _cardPaymentStatus.postValue(Resource.success(result.data))
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


    fun checkTransactionStatus(trackingId: String){
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

    fun mobileMoneyTransactionStatusBackground(trackingId: String){
        _transactionStatusBg.postValue(Resource.loading("Confirming payment ... "))
        viewModelScope.launch {
            val result = paymentRepository.getTransactionStatus(trackingId)
            when(result.status){
                Status.ERROR -> {
                    _transactionStatusBg.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _transactionStatusBg.postValue(Resource.success(result.data))
                }
                else -> {}
            }

        }
    }


    fun serverJwt(requestServerJwt: RequestServerJwt){
        _serverJwt.postValue(Resource.loading("Initializing txn ... "))
        viewModelScope.launch {
            val result = paymentRepository.serverJwt(requestServerJwt)
            when(result.status){
                Status.ERROR -> {
                    _serverJwt.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _serverJwt.postValue(Resource.success(result.data))
                }
                else -> {}
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




    fun check3ds(checkDSecureRequest: CheckDSecureRequest, token: String) {
        _dsResponse.postValue(Resource.loading("Initiating payment process ... "))
        viewModelScope.launch {
            val result = paymentRepository.check3ds(checkDSecureRequest,token)
            when (result.status) {
                Status.ERROR -> {
                    _dsResponse.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _dsResponse.postValue(Resource.success(result.data))
                }
                else -> {}
            }
        }
    }



    fun getDsToken(dsTokenRequest: DsTokenRequest) {
        _dsToken.postValue(Resource.loading("Initiating payment process ... "))
        viewModelScope.launch {
            val result = paymentRepository.dsToken(dsTokenRequest)
            when (result.status) {
                Status.ERROR -> {
                    _dsToken.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _dsToken.postValue(Resource.success(result.data))
                }
                else -> {}
            }
        }
    }


}