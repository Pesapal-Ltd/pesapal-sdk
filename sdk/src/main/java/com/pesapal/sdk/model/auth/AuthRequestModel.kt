package com.pesapal.sdk.model.auth

import com.google.gson.annotations.SerializedName
import com.pesapal.sdk.model.fingerprinting.DeviceFingerPrintModel


internal data class AuthRequestModel(
     var consumer_key: String?,
     var consumer_secret: String?,
     @SerializedName("device_metadata")
     var deviceMetadata : DeviceFingerPrintModel
)