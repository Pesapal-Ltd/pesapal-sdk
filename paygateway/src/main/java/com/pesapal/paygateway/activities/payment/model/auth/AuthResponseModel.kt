package com.pesapal.paygateway.activities.payment.model.auth
import com.pesapal.paygateway.activities.payment.model.mobile_money.Error

data class AuthResponseModel(val expiryDate: String = "",
                             val error: Error,
                             val message: String = "",
                             val token: String = "",
                             val status: String?)