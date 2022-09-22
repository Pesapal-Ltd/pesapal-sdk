package com.pesapal.paymentgateway.payment.model.auth

data class AuthResponseModel(val expiryDate: String = "",
                             val error: Error,
                             val message: String = "",
                             val token: String = "",
                             val status: String?)