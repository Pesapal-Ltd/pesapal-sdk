package com.pesapal.sdk.activities.payment.model.payment

import java.math.BigDecimal

data class PaymentDetails(
    var order_id: String? = "",
    var amount: BigDecimal = BigDecimal.ONE,
    var order_tracking_id: String? = "",
    var currency: String? = "",
    var accountNumber: String? = "",
    var callbackUrl: String? = "",
    var consumerSessionId: String? = "",
    var consumer_key: String? = "",
    var consumer_secret: String? = "",
    var ipn_url: String? = "",
    var ipn_notification_type: String? = "GET",
)