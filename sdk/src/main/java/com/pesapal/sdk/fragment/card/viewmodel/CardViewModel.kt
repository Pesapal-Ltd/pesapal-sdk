package com.pesapal.sdk.fragment.card.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.sdk.fragment.card.repo.CardRepository
import com.pesapal.sdk.model.card.CardinalRequest
import com.pesapal.sdk.model.card.CardinalResponse
import com.pesapal.sdk.model.card.order_id.request.CardOrderTrackingIdRequest
import com.pesapal.sdk.model.card.order_id.response.CardOrderTrackingIdResponse
import com.pesapal.sdk.model.card.submit.request.SubmitCardRequest
import com.pesapal.sdk.model.card.submit.response.SubmitCardResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.Status
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


    private var _cardinalToken = MutableLiveData<Resource<CardinalResponse>>()
    val cardinalToken: LiveData<Resource<CardinalResponse>>
        get() = _cardinalToken


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
            when(result.status){
                Status.ERROR -> {
                    _submitCardResponse.postValue(Resource.error(result.message!!))
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
                    _cardPaymentStatus.postValue(Resource.error(result.message!!))
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
     * Retrieve jwt token
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

}