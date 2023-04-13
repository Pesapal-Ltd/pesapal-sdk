package com.pesapal.sdk.model.card.submit.response

import com.google.gson.annotations.SerializedName
import com.pesapal.sdk.model.txn_status.TransactionError

data class SubmitCardResponse(@SerializedName("merchant_reference")
                              val merchantReference: String = "",
                              @SerializedName("order_tracking_id")
                              val orderTrackingId: String = "",
                              @SerializedName("error")
                              val error: TransactionError? = null,
                              @SerializedName("status")
                              val status: String = "",
                              @SerializedName("call_back_url")
                              val callBackUrl: String = "")