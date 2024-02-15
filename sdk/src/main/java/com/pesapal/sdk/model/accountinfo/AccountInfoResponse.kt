package com.pesapal.sdk.model.accountinfo

import com.google.gson.annotations.SerializedName

data class AccountInfoResponse(
    @SerializedName("merchant_name")
    val merchantName: String?,
    val message: String?,
    val status: String?
)