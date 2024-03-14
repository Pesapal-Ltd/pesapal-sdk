package com.pesapal.sdk.model.fingerprinting

import com.google.gson.annotations.SerializedName

internal data class KeyInfo(@SerializedName("modulus")
                   val modulus: String = "",
                   @SerializedName("exponent")
                   val exponent: String = "")