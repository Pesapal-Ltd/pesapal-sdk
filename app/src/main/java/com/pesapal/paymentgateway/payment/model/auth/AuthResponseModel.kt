package com.pesapal.paymentgateway.payment.model.auth

data class AuthResponseModel(val expiryDate: String = "",
                             val error: String = "",
                             val message: String = "",
                             val token: String = "",
                             val status: String = "")