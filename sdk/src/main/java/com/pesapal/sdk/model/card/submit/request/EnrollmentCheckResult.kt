package com.pesapal.sdk.model.card.submit.request

import com.google.gson.annotations.SerializedName

data class EnrollmentCheckResult(@SerializedName("authenticationResult")
                                 val authenticationResult: String? = null,
                                 @SerializedName("authentication_attempted")
                                 val authenticationAttempted: Boolean = false,
                                 @SerializedName("directory_server_transaction_id")
                                 val directoryServerTransactionId: String? = null,
                                 @SerializedName("cavv_algorithm")
                                 val cavvAlgorithm: String? = null,
                                 @SerializedName("eci")
                                 val eci: String? = null,
                                 @SerializedName("eci_raw")
                                 val eciRaw: String? = null,
                                 @SerializedName("cavv")
                                 val cavv: String? = null,
                                 @SerializedName("reason_code")
                                 val reasonCode: String? = null,
                                 @SerializedName("processor_transaction_id")
                                 val processorTransactionId: String? = null,
                                 @SerializedName("xid")
                                 val xid: String? = null,
                                 @SerializedName("ucafCollectionIndicator")
                                 val ucafCollectionIndicator: String? = null,
                                 @SerializedName("threeDS_server_transaction_id")
                                 val threeDSServerTransactionId: String? = null,
                                 @SerializedName("pareq")
                                 val pareq: String? = null,
                                 @SerializedName("ucafAuthenticationData")
                                 val ucafAuthenticationData: String? = null,
                                 @SerializedName("veresEnrolled")
                                 val veresEnrolled: String? = null,
                                 @SerializedName("aav")
                                 val aav: String? = null,
                                 @SerializedName("specification_version")
                                 val specificationVersion: String? = null,
                                 @SerializedName("commerce_indicator")
                                 val commerceIndicator: String? = null,
                                 @SerializedName("pares_status")
                                 val paresStatus: String? = null,
                                 @SerializedName("checkout_session_id")
                                 val checkoutSessionId: String = "")