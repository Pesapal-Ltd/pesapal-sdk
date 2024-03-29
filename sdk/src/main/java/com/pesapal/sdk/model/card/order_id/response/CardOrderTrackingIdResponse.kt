package com.pesapal.sdk.model.card.order_id.response

import com.google.gson.annotations.SerializedName
import com.pesapal.sdk.model.txn_status.TransactionError

internal data class CardOrderTrackingIdResponse(@SerializedName("payment_message")
                               val paymentMessage: String = "",
                                       @SerializedName("account_number")
                               val accountNumber: String = "",
                                       @SerializedName("merchant_reference")
                               val merchantReference: String = "",
                                       @SerializedName("order_tracking_id")
                               val orderTrackingId: String = "",
                                       @SerializedName("business_number")
                               val businessNumber: String = "",
                                       @SerializedName("error")
                               val error: TransactionError?,
                                       @SerializedName("redirect_url")
                               val redirectUrl: String = "",
                                       @SerializedName("status")
                               val status: String = "",
                                                @SerializedName("payment_method")
                                                val paymentMethod: String? = null,
                                                @SerializedName("currency")
                                                val currency: String? = null,
                                                @SerializedName("created_date")
                                                val createdDate: String = "",
                                                @SerializedName("confirmation_code")
                                                val confirmationCode: String? = null,
                                                @SerializedName("amount")
                                                val amount: Int = 0,
                                                @SerializedName("status_code")
                                                val statusCode: Int = 0,
                                                @SerializedName("payment_account")
                                                val paymentAccount: String? = null,
)