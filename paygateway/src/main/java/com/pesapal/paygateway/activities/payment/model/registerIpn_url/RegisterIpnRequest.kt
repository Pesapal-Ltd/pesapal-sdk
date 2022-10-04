package com.pesapal.paygateway.activities.payment.model.registerIpn_url

data class RegisterIpnRequest (
    var url: String,
    var ipn_notification_type: String,
)