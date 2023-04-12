package com.pesapal.sdk.activities.payment.model.card.submit.request

import com.google.gson.annotations.SerializedName
import com.pesapal.sdk.activities.payment.model.card.BillingAddress

data class SubmitCardRequest(@SerializedName("cvv")
                               val cvv: String = "",
                             @SerializedName("enrollment_check_result")
                               val enrollmentCheckResult: EnrollmentCheckResult,
                             @SerializedName("subscription_details")
                               val subscriptionDetails: SubscriptionDetails,
                             @SerializedName("order_tracking_id")
                               val orderTrackingId: String? = "",
                             @SerializedName("expiryMonth")
                               val expiryMonth: String = "",
                             @SerializedName("billing_address")
                               val billingAddress: BillingAddress,
                             @SerializedName("expiryYear")
                               val expiryYear: String = "",
                             @SerializedName("ip_address")
                               val ipAddress: String = "",
                             @SerializedName("cardNumber")
                               val cardNumber: String = "")