package com.pesapal.paygateway.activities.payment.model.mobile_money

import com.google.gson.annotations.SerializedName
import com.pesapal.paygateway.activities.payment.model.mobile_money.Error

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
                               val error: Error?,
                               @SerializedName("redirect_url")
                               val redirectUrl: String = "",
                               @SerializedName("status")
                               val status: String?)