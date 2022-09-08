package com.pesapal.paymentgateway.payment.model.RegisterIpnUrl

data class RegisterIpnRequest (
    var url: String,
    var ipn_notification_type: String,
)