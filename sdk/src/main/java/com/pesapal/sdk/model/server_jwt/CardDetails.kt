package com.pesapal.sdk.model.server_jwt

import com.google.gson.annotations.SerializedName

data class CardDetails(@SerializedName("expiry_month")
                       val expiryMonth: Int = 0,
                       @SerializedName("cvv")
                       val cvv: String = "",
                       @SerializedName("card_number")
                       val cardNumber: String = "",
                       @SerializedName("expiry_year")
                       val expiryYear: Int = 0
)