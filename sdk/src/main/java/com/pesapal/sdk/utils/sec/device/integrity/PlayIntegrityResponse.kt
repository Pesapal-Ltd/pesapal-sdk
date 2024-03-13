package com.pesapal.sdk.utils.sec.device.integrity

import com.google.gson.annotations.SerializedName
import com.pesapal.sdk.utils.sec.device.integrity.KeyInfo

data class PlayIntegrityResponse(@SerializedName("status_description")
                                 val statusDescription: String = "",
                                 @SerializedName("is_active")
                                 val isActive: Int = 0,
                                 @SerializedName("friendly_message")
                                 val friendlyMessage: String = "",
                                 @SerializedName("key_info")
                                 val keyInfo: KeyInfo,
                                 @SerializedName("created_date")
                                 val createdDate: String = "",
                                 @SerializedName("message")
                                 val message: String = "",
                                 @SerializedName("modified_date")
                                 val modifiedDate: String = "",
                                 @SerializedName("status")
                                 val status: String = "")