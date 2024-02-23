package com.pesapal.sdk.model.txn_status

import com.google.gson.annotations.SerializedName

data class TransactionStatusResponse(@SerializedName("amount")
                                     val amount: Int = 0,
                                     @SerializedName("status_code")
                                     val statusCode: Int = 0,
                                     @SerializedName("confirmation_code")
                                     val confirmationCode: String? = null,
                                     @SerializedName("order_tracking_id")
                                     val orderTrackingId: String? = null,
                                     @SerializedName("description")
                                     val description: String? = null,
                                     @SerializedName("message")
                                     val message: String? = null,
                                     @SerializedName("error")
                                     val error: TransactionError? = null,
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



/**
 * {
 *   "payment_method": "MpesaKE",
 *   "amount": 1.0,
 *   "created_date": "2024-02-22T22:18:20.337",
 *   "confirmation_code": "41784283",
 *   "order_tracking_id": "e26a9c93-d016-4d97-947c-dd8e164757d3",
 *   "payment_status_description": "Failed",
 *   "description": null,
 *   "message": "Request processed successfully",
 *   "payment_account": "2547xxx18241",
 *   "call_back_url": "http://localhost:56522?OrderTrackingId=e26a9c93-d016-4d97-947c-dd8e164757d3&OrderMerchantReference=752E6A48",
 *   "status_code": 2,
 *   "merchant_reference": "752E6A48",
 *   "payment_status_code": "",
 *   "currency": "KES",
 *   "payer_info": {
 *     "first_name": "Samuel",
 *     "last_name": "Nyamai"
 *   },
 *   "error": {
 *     "error_type": null,
 *     "code": null,
 *     "message": null
 *   },
 *   "status": "200"
 * }
 */



// Failed

/**
 * {
 *   "payment_method": "MpesaKE",
 *   "amount": 1.0,
 *   "created_date": "2024-02-22T22:21:38.623",
 *   "confirmation_code": "SBM2VWCQ5G",
 *   "order_tracking_id": "e26a9c93-d016-4d97-947c-dd8e164757d3",
 *   "payment_status_description": "Completed",
 *   "description": null,
 *   "message": "Request processed successfully",
 *   "payment_account": "2547xxx18241",
 *   "call_back_url": "http://localhost:56522?OrderTrackingId=e26a9c93-d016-4d97-947c-dd8e164757d3&OrderMerchantReference=752E6A48",
 *   "status_code": 1,
 *   "merchant_reference": "752E6A48",
 *   "payment_status_code": "",
 *   "currency": "KES",
 *   "payer_info": {
 *     "first_name": "Samuel",
 *     "last_name": "Nyamai"
 *   },
 *   "error": {
 *     "error_type": null,
 *     "code": null,
 *     "message": null
 *   },
 *   "status": "200"
 * }
 */