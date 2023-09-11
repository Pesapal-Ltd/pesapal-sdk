package com.pesapal.sdk.fragment.mobile_money.mpesa.stk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pesapal.sdk.fragment.mobile_money.mpesa.repo.MpesaRepository
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.mobile_money.MobileMoneyRequest
import com.pesapal.sdk.model.mobile_money.MobileMoneyResponse
import com.pesapal.sdk.model.txn_status.TransactionStatusResponse
import com.pesapal.sdk.utils.Resource
import com.pesapal.sdk.utils.Status
import kotlinx.coroutines.launch

internal class MpesaPesapalViewModel : ViewModel() {

    var mpesaRepository = MpesaRepository()

    private var _mobileMoneyResponse = MutableLiveData<Resource<MobileMoneyResponse>>()
    val mobileMoneyResponse: LiveData<Resource<MobileMoneyResponse>>
        get() = _mobileMoneyResponse

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


}