package com.pesapal.paygateway.activities.payment.model.cacontinie

import com.google.gson.annotations.SerializedName

data class PayloadCanContinueModel(@SerializedName("CCAExtension")
                                   val cCAExtension: CCAExtension,
                                   @SerializedName("Consumer")
                                   val consumer: Consumer,
                                   @SerializedName("OrderDetails")
                                   val orderDetails: OrderDetails,
                                   @SerializedName("Payload")
                                   val payload: String = "",
                                   @SerializedName("AcsUrl")
                                   val acsUrl: String? = "")