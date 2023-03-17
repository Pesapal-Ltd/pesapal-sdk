package com.pesapal.paygateway.activities.payment.model.card.submit.response

import com.google.gson.annotations.SerializedName
import com.pesapal.paygateway.activities.payment.model.mobile_money.Error

data class SubmitCardResponse(@SerializedName("merchant_reference")
                              val merchantReference: String = "",
                              @SerializedName("order_tracking_id")
                              val orderTrackingId: String = "",
                              @SerializedName("error")
                              val error: Error? = null,
                              @SerializedName("status")
                              val status: String = "",
                              @SerializedName("call_back_url")
                              val callBackUrl: String = "")