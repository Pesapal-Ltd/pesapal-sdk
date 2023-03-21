package com.pesapal.paygateway.activities.payment.model.card.status.response

import com.google.gson.annotations.SerializedName
import com.pesapal.paygateway.activities.payment.model.error.TransactionError

data class CheckCardPaymentStatusResponse(@SerializedName("payment_message")
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
                               val error: TransactionError?,
                                          @SerializedName("redirect_url")
                               val redirectUrl: String = "",
                                          @SerializedName("status")
                               val status: String?)