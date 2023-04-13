package com.pesapal.sdk.model.server_jwt.response

import com.google.gson.annotations.SerializedName

data class Payload(@SerializedName("BillingAddress")
                   val billingAddress: BillingAddress,
                   @SerializedName("Account")
                   val account: Account,
                   @SerializedName("Consumer")
                   val consumer: Consumer,
                   @SerializedName("OrderDetails")
                   val orderDetails: OrderDetails)