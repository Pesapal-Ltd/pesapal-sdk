package com.pesapal.sdk.model.auth

import com.google.gson.annotations.SerializedName
import com.pesapal.sdk.model.txn_status.TransactionError
import com.pesapal.sdk.model.fingerprinting.KeyInfo

internal data class AuthResponseModel(
    @SerializedName("key_info")
    val keyInfo: KeyInfo?,
    val expiryDate: String = "",
    val error: TransactionError,
    val message: String = "",
    val token: String = "",
    val status: String?)