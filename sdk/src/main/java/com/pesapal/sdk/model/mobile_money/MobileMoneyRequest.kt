package com.pesapal.sdk.model.mobile_money

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.pesapal.sdk.model.card.BillingAddress
import java.io.Serializable
import java.math.BigDecimal

internal data class MobileMoneyRequest(@SerializedName("account_number")
                              val accountNumber: String = "",
                              @SerializedName("amount")
                              val amount: BigDecimal = BigDecimal.ZERO,
                              @SerializedName("source_channel")
                              val sourceChannel: Int = 0,
                              @SerializedName("cancellation_url")
                              val cancellationUrl: String = "",
                              @SerializedName("description")
                              val description: String = "",
                              @SerializedName("language")
                              val language: String = "",
                              @SerializedName("billing_address")
                              val billingAddress: BillingAddress,
                              @SerializedName("notification_id")
                              val notificationId: String = "",
                              @SerializedName("callback_url")
                              val callbackUrl: String = "",
                              @SerializedName("payment_method_id")
                              val paymentMethodId: Int = 0,
                              @SerializedName("currency")
                              val currency: String = "",
                              @SerializedName("id")
                              val id: String = "",
                              @SerializedName("terms_and_conditions_id")
                              val termsAndConditionsId: String = "",
                              @SerializedName("msisdn")
                              val msisdn: String? = "",
                              @SerializedName("allowed_currencies")
                              val allowedCurrencies: String = "",
                              @SerializedName("tracking_id")
                              var trackingId: String = "",
                              @SerializedName("charge_request")
                              var chargeRequest: Boolean = true

): Serializable