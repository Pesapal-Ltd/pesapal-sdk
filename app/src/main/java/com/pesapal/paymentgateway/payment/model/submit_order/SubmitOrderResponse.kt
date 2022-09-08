package com.pesapal.paymentgateway.payment.model.submit_order

data class SubmitOrderResponse(
    val order_tracking_id: String,
    val merchant_reference: String,
    val redirect_url: String,
    val error: String,
    val status: String,
)


