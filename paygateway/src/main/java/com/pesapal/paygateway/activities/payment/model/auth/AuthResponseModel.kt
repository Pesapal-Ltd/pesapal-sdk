package com.pesapal.paygateway.activities.payment.model.auth

import com.pesapal.paygateway.activities.payment.model.error.TransactionError

data class AuthResponseModel(val expiryDate: String = "",
                             val error: TransactionError,
                             val message: String = "",
                             val token: String = "",
                             val status: String?)