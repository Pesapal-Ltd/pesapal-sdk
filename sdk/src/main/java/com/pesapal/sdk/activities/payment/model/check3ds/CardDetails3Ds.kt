package com.pesapal.sdk.activities.payment.model.check3ds

import com.google.gson.annotations.SerializedName

data class CardDetails3Ds(@SerializedName("cvv")
                       val cvv: String = "",
                          @SerializedName("card_number")
                       val cardNumber: String = "",
                          @SerializedName("month")
                       val month: String = "",
                          @SerializedName("year")
                       val year: Int = 0,
                          @SerializedName("card_type")
                       val cardType: String? = null)