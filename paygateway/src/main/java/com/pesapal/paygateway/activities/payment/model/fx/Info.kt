package com.pesapal.paygateway.activities.payment.model.fx

import com.google.gson.annotations.SerializedName

data class Info(@SerializedName("quote")
                val quote: Double = 0.0,
                @SerializedName("timestamp")
                val timestamp: String = "")