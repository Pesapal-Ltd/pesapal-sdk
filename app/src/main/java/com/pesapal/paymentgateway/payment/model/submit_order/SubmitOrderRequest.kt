package com.pesapal.paymentgateway.payment.model.submit_order

import com.google.gson.annotations.SerializedName

data class SubmitOrderRequest(@SerializedName("callback_url")
                              val callbackUrl: String = "",
                              @SerializedName("amount")
                              val amount: Int = 0,
                              @SerializedName("description")
                              val description: String = "",
                              @SerializedName("currency")
                              val currency: String = "",
                              @SerializedName("billing_address")
                              val billingAddress: BillingAddress,
                              @SerializedName("id")
                              val id: String = "",
                              @SerializedName("notification_id")
                              val notificationId: String = "")