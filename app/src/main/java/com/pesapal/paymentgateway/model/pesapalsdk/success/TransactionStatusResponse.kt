package com.pesapal.paymentgateway.model.pesapalsdk.success

import com.google.gson.annotations.SerializedName

data class TransactionStatusResponse(@SerializedName("amount")
                                     val amount: Int = 0,
                                     @SerializedName("status_code")
                                     val statusCode: Int = 0,
                                     @SerializedName("confirmation_code")
                                     val confirmationCode: String? = null,
                                     @SerializedName("description")
                                     val description: String? = null,
                                     @SerializedName("message")
                                     val message: String? = null,
                                     @SerializedName("error")
                                     val error: Error? = null,
                                     @SerializedName("payment_account")
                                     val paymentAccount: String? = null,
                                     @SerializedName("merchant_reference")
                                     val merchantReference: String? = null,
                                     @SerializedName("payment_status_code")
                                     val paymentStatusCode: String? = null,
                                     @SerializedName("currency")
                                     val currency: String? = null,
                                     @SerializedName("created_date")
                                     val createdDate: String = "",
                                     @SerializedName("payment_status_description")
                                     val paymentStatusDescription: String? = null,
                                     @SerializedName("payment_method")
                                     val paymentMethod: String? = null,
                                     @SerializedName("call_back_url")
                                     val callBackUrl: String? = null,
                                     @SerializedName("status")
                                     val status: String ?): java.io.Serializable