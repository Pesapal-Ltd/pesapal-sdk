package com.pesapal.sdk.fragment.card.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pesapal.paygateway.activities.payment.model.check3ds.CheckDSecureRequest
import com.pesapal.paygateway.activities.payment.model.check3ds.response.CheckDsResponse
import com.pesapal.paygateway.activities.payment.model.check3ds.token.DsTokenRequest
import com.pesapal.sdk.fragment.card.repo.CardRepository
import com.pesapal.sdk.model.auth.AuthResponseModel
import com.pesapal.sdk.model.card.CardinalRequest
import com.pesapal.sdk.model.card.CardinalResponse
import com.pesapal.sdk.model.card.RequestServerJwt
import com.pesapal.sdk.model.card.ResponseServerJwt
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.model.card.submit.response.SubmitCardResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.Status
import com.pesapal.sdk.utils.sec.EncryptRequests.encryptWithPublicKey
import com.pesapal.sdk.utils.sec.model.EncModel
import kotlinx.coroutines.launch

internal class CardViewModel : ViewModel() {

    private var cardRepository = CardRepository()

    private var _cardOrderTrackingIdResponse = MutableLiveData<Resource<CardOrderTrackingIdResponse>>()
    val cardOrderTrackingIdResponse: LiveData<Resource<CardOrderTrackingIdResponse>>
        get() = _cardOrderTrackingIdResponse

    private var _submitCardResponse = MutableLiveData<Resource<SubmitCardResponse>>()
    val submitCardResponse: LiveData<Resource<SubmitCardResponse>>
        get() = _submitCardResponse


    private var _cardPaymentStatus = MutableLiveData<Resource<TransactionStatusResponse>>()
    val cardPaymentStatus: LiveData<Resource<TransactionStatusResponse>>
        get() = _cardPaymentStatus


    //TODO DELETE THIS LATER
    private var _cardinalToken = MutableLiveData<Resource<CardinalResponse>>()
    val cardinalToken: LiveData<Resource<CardinalResponse>>
        get() = _cardinalToken

    private var _serverJwt = MutableLiveData<Resource<ResponseServerJwt>>()
    val serverJwt: LiveData<Resource<ResponseServerJwt>>
        get() = _serverJwt

    private var _dsToken = MutableLiveData<Resource<AuthResponseModel>>()
    val dsToken: LiveData<Resource<AuthResponseModel>>
        get() = _dsToken

    private var _dsResponse = MutableLiveData<Resource<CheckDsResponse>>()
    val dsResponse: LiveData<Resource<CheckDsResponse>>
        get() = _dsResponse


    fun generateCardOrderTrackingId(cardOrderTrackingIdRequest: CardOrderTrackingIdRequest, action: String){
        _cardOrderTrackingIdResponse.postValue(Resource.loading(action))
        viewModelScope.launch {
            val result = cardRepository.generateCardOrderTrackingId(cardOrderTrackingIdRequest)
            when(result.status){
                Status.ERROR -> {
                    _cardOrderTrackingIdResponse.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _cardOrderTrackingIdResponse.postValue(Resource.success(result.data))
                }
                else -> {
                    _cardOrderTrackingIdResponse.postValue(Resource.error(result.message!!))
                }
            }

        }
    }

    fun submitCardRequest(submitCardRequest: SubmitCardRequest){
        _submitCardResponse.postValue(Resource.loading("Processing request"))
        viewModelScope.launch {
            val result = cardRepository.submitCardRequest(submitCardRequest)
            val encryptedData = encryptWithPublicKey(Gson().toJson(submitCardRequest))

//            val result =EncModel(encryptedData)
            when(result.status){
                Status.ERROR -> {
                    _submitCardResponse.postValue(Resource.error(result.message!!, result.data))
                }
                Status.SUCCESS -> {
                    _submitCardResponse.postValue(Resource.success(result.data))
                }
                else -> {
                }
            }

        }
    }


    fun checkCardPaymentStatus(trackingId: String){
        _cardPaymentStatus.postValue(Resource.loading("Confirming payment ... "))
        viewModelScope.launch {
            val result = cardRepository.getCardTransactionStatus(trackingId)
            when(result.status){
                Status.ERROR -> {
                    _cardPaymentStatus.postValue(Resource.error(result.message!!, result.data))
                }
                Status.SUCCESS -> {
                    _cardPaymentStatus.postValue(Resource.success(result.data))
                }
                else -> {
                    _cardPaymentStatus.postValue(Resource.error(result.message!!))
                }
            }

        }
    }

    /**
     * Retrieve jwt token. OLDER VERSION
     */
    fun getCardinalToken(cardinalRequest: CardinalRequest){
        _cardinalToken.postValue(Resource.loading("Preparing paymen ... "))
        viewModelScope.launch {
            val result = cardRepository.getCardinalToken(cardinalRequest)
            when(result.status){
                Status.ERROR -> {
                    _cardinalToken.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _cardinalToken.postValue(Resource.success(result.data))
                }
                else -> {
                    _cardinalToken.postValue(Resource.error(result.message!!))
                }
            }

        }
    }


    fun serverJwt(requestServerJwt: RequestServerJwt){
        _serverJwt.postValue(Resource.loading("Initializing txn ... "))
        viewModelScope.launch {
            val result = cardRepository.serverJwt(requestServerJwt)
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

    fun getDsToken(dsTokenRequest: DsTokenRequest) {
        _dsToken.postValue(Resource.loading("Initiating payment process ... "))
        viewModelScope.launch {
            val result = cardRepository.dsToken(dsTokenRequest)
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

    fun check3ds(checkDSecureRequest: CheckDSecureRequest, token: String) {
        _dsResponse.postValue(Resource.loading("Initiating payment process ... "))
        viewModelScope.launch {
            val result = cardRepository.check3ds(checkDSecureRequest,token)
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

}