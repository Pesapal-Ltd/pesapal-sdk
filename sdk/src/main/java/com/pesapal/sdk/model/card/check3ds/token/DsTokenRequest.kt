package com.pesapal.paygateway.activities.payment.model.check3ds.token

data class DsTokenRequest(
    val consumer_key: String,
    val consumer_secret: String
)