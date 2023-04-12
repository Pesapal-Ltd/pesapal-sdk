package com.pesapal.sdk.activities.payment.model.server_jwt.response

import com.google.gson.annotations.SerializedName

data class OriginalPayloadModel(@SerializedName("ObjectifyPayload")
                                var objectifyPayload: Boolean = false,
                                @SerializedName("OrgUnitId")
                                val orgUnitId: String = "",
                                @SerializedName("ReferenceId")
                                val referenceId: String = "",
                                @SerializedName("iss")
                                val iss: String = "",
                                @SerializedName("ConfirmUrl")
                                val confirmUrl: String = "",
                                @SerializedName("Payload")
                                val payload: Payload,
                                @SerializedName("exp")
                                val exp: Int = 0,
                                @SerializedName("iat")
                                val iat: Int = 0,
                                @SerializedName("jti")
                                val jti: String = "")