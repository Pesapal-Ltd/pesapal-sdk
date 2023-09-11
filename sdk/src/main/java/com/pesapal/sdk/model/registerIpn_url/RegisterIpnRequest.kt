package com.pesapal.sdk.model.registerIpn_url

internal data class RegisterIpnRequest (
    var url: String,
    var ipn_notification_type: String,
)