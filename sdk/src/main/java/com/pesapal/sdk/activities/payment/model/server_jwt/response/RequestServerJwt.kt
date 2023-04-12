package com.pesapal.sdk.activities.payment.model.server_jwt.response

import com.google.gson.annotations.SerializedName

data class RequestServerJwt(@SerializedName("original_payload")
                            val originalPayload: String = "",
                            @SerializedName("merchant_reference")
                            val merchantReference: String = "",
                            @SerializedName("order_jwt")
                            val orderJwt: String = "",
                            @SerializedName("message")
                            val message: String = "",
                            @SerializedName("status")
                            val status: String = "")