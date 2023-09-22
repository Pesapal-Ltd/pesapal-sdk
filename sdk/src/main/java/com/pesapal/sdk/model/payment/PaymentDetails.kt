package com.pesapal.sdk.model.payment

import com.pesapal.sdk.utils.PESAPALAPI3SDK
import java.io.Serializable
import java.math.BigDecimal

internal class PaymentDetails(
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
    var country: PESAPALAPI3SDK.COUNTRIES_ENUM? = null,
    var mobile_provider:BigDecimal? = BigDecimal.ONE,
): Serializable