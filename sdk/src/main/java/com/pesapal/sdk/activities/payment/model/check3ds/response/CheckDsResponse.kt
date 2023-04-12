package com.pesapal.sdk.activities.payment.model.check3ds.response

import com.google.gson.annotations.SerializedName

data class CheckDsResponse(@SerializedName("authenticationResult")
                           val authenticationResult: String? = null,
                           @SerializedName("authentication_transaction_id")
                           val authenticationTransactionId: String = "",
                           @SerializedName("directory_server_transaction_id")
                           val directoryServerTransactionId: String? = null,
                           @SerializedName("cavv_algorithm")
                           val cavvAlgorithm: String? = null,
                           @SerializedName("is_card_enrolled")
                           val isCardEnrolled: Boolean = false,
                           @SerializedName("eci")
                           val eci: String? = null,
                           @SerializedName("acs_url")
                           val acsUrl: String = "",
                           @SerializedName("eci_raw")
                           val eciRaw: String? = null,
                           @SerializedName("reason_code")
                           val reasonCode: String = "",
                           @SerializedName("cavv")
                           val cavv: String? = null,
                           @SerializedName("xid")
                           val xid: String = "",
                           @SerializedName("ucafCollectionIndicator")
                           val ucafCollectionIndicator: String? = null,
                           @SerializedName("threeDS_server_transaction_id")
                           val threeDSServerTransactionId: String? = null,
                           @SerializedName("payload")
                           val payload: String = "",
                           @SerializedName("ucafAuthenticationData")
                           val ucafAuthenticationData: String? = null,
                           @SerializedName("pareq")
                           val pareq: String = "",
                           @SerializedName("veresEnrolled")
                           val veresEnrolled: String = "",
                           @SerializedName("specification_version")
                           val specificationVersion: String = "",
                           @SerializedName("commerce_indicator")
                           val commerceIndicator: String? = null,
                           @SerializedName("pares_status")
                           val paresStatus: String? = null,
                           @SerializedName("response_payload")
                           val responsePayload: String = "")