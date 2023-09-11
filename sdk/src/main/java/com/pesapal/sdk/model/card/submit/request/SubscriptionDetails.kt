package com.pesapal.sdk.model.card.submit.request

import com.google.gson.annotations.SerializedName

internal data class SubscriptionDetails(@SerializedName("end_date")
                               val endDate: String = "",
                               @SerializedName("amount")
                               val amount: Int = 0,
                               @SerializedName("account_reference")
                               val accountReference: String? = null,
                               @SerializedName("frequency")
                               val frequency: Int = 0,
                               @SerializedName("start_date")
                               val startDate: String = "")