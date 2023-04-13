package com.pesapal.sdk.model.server_jwt.response

import com.google.gson.annotations.SerializedName

data class OrderDetails(@SerializedName("CurrencyCode")
                        val currencyCode: String = "",
                        @SerializedName("Amount")
                        val amount: String = "",
                        @SerializedName("OrderNumber")
                        val orderNumber: String = "")