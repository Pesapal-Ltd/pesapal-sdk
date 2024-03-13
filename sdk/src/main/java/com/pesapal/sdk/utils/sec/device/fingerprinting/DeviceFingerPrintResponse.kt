package com.pesapal.sdk.utils.sec.device.fingerprinting

import com.google.gson.annotations.SerializedName

data class DeviceFingerPrintResponse(@SerializedName("status_description")
                                     val statusDescription: String = "",
                                     @SerializedName("is_active")
                                     val isActive: Int = 0,
                                     @SerializedName("friendly_message")
                                     val friendlyMessage: String = "",
                                     @SerializedName("created_date")
                                     val createdDate: String = "",
                                     @SerializedName("message")
                                     val message: String = "",
                                     @SerializedName("modified_date")
                                     val modifiedDate: String = "",
                                     @SerializedName("hash_id")
                                     val hashId: String = "",
                                     @SerializedName("status")
                                     val status: String = ""
)