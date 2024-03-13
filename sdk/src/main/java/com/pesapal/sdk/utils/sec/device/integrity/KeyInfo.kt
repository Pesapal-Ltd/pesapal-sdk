package com.pesapal.sdk.utils.sec.device.integrity

import com.google.gson.annotations.SerializedName

data class KeyInfo(@SerializedName("modulus")
                   val modulus: String = "",
                   @SerializedName("exponent")
                   val exponent: String = "")