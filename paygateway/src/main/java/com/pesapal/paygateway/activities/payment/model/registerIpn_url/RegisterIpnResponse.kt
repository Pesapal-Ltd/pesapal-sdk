package com.pesapal.paygateway.activities.payment.model.registerIpn_url

import com.pesapal.paygateway.activities.payment.model.txn_status.TransactionError

data class RegisterIpnResponse(
    val id: String,
    val url: String,
    val created_date: String,
    val ipn_id: String,
    val notification_type: String,
    val ipn_notification_type_description: String,
    val ipn_status: String,
    val ipn_status_decription: String,
    val error: TransactionError,
    val status: String?,
)