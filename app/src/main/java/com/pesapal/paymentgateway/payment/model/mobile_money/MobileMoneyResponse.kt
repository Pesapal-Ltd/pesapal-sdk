package com.pesapal.paymentgateway.payment.model.mobile_money

import com.google.gson.annotations.SerializedName

data class MobileMoneyResponse(@SerializedName("payment_message")
                               val paymentMessage: String = "",
                               @SerializedName("account_number")
                               val accountNumber: String = "",
                               @SerializedName("merchant_reference")
                               val merchantReference: String = "",
                               @SerializedName("order_tracking_id")
                               val orderTrackingId: String = "",
                               @SerializedName("business_number")
                               val businessNumber: String = "",
                               @SerializedName("error")
                               val error: String = "",
                               @SerializedName("redirect_url")
                               val redirectUrl: String = "",
                               @SerializedName("status")
                               val status: String = "")