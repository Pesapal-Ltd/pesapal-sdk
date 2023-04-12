package com.pesapal.sdk.activities.payment.model.cacontinie

import com.google.gson.annotations.SerializedName

data class OrderDetails(@SerializedName("CurrencyCode")
                        val currencyCode: String = "",
                        @SerializedName("OrderChannel")
                        val orderChannel: String = "",
                        @SerializedName("Amount")
                        val amount: String = "",
                        @SerializedName("OrderNumber")
                        val orderNumber: String = "",
                        @SerializedName("TransactionId")
                        val transactionId: String = "")