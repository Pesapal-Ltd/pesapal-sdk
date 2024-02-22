package com.pesapal.sdk.fragment.mobile_money.mpesa.pending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.sdk.fragment.mobile_money.mpesa.repo.MpesaRepository
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.Status
import kotlinx.coroutines.launch

internal class MpesaPendingViewModel : ViewModel(){

    var mpesaRepository = MpesaRepository()

    private var _mobileMoneyResponse = MutableLiveData<Resource<MobileMoneyResponse>>()
    val mobileMoneyResponse: LiveData<Resource<MobileMoneyResponse>>
        get() = _mobileMoneyResponse

    private var _transactionStatus = MutableLiveData<Resource<TransactionStatusResponse>>()
    val transactionStatus: LiveData<Resource<TransactionStatusResponse>>
        get() = _transactionStatus

    private var _transactionStatusBg = MutableLiveData<Resource<TransactionStatusResponse>>()
    val transactionStatusBg: LiveData<Resource<TransactionStatusResponse>>
        get() = _transactionStatusBg

    fun sendMobileMoneyCheckOut(mobileMoneyRequest: MobileMoneyRequest, action: String){
        _mobileMoneyResponse.postValue(Resource.loading(action))
        viewModelScope.launch {
            val result = mpesaRepository.mobileMoneyApi(mobileMoneyRequest)
            when(result.status){
                Status.ERROR -> {
                    _mobileMoneyResponse.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    _mobileMoneyResponse.postValue(Resource.success(result.data))
                }
                else -> {
                }
            }

        }
    }

    fun mobileMoneyTransactionStatus(trackingId: String){
        _transactionStatus.postValue(Resource.loading("Confirming payment ... "))
        viewModelScope.launch {
            val result = mpesaRepository.getTransactionStatus(trackingId)
            when(result.status){
                Status.ERROR -> {
                    _transactionStatus.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    if(result.data!!.paymentStatusDescription == "Completed") {
                        _transactionStatus.postValue(Resource.success(result.data))
                    }else{
                        _transactionStatus.postValue(Resource.error("Awaiting payment .."))
                    }
                }
                else -> {
                }
            }

        }
    }

    fun mobileMoneyTransactionStatusBackground(trackingId: String){
        _transactionStatusBg.postValue(Resource.loading("Confirming payment ... "))
        viewModelScope.launch {
            val result = mpesaRepository.getTransactionStatus(trackingId)
            when(result.status){
                Status.ERROR -> {
                    _transactionStatusBg.postValue(Resource.error(result.message!!))
                }
                Status.SUCCESS -> {
                    if(result.data!!.paymentStatusDescription == "Completed") {
                        _transactionStatusBg.postValue(Resource.success(result.data))
                    }else{
                        _transactionStatusBg.postValue(Resource.error("Awaiting payment ..", result.data))
                    }
                }
                else -> {
                }
            }

        }
    }

}