package com.pesapal.sdk.model.cacontinie

import com.google.gson.annotations.SerializedName

data class Account(@SerializedName("NameOnAccount")
                   val nameOnAccount: String = "",
                   @SerializedName("CardCode")
                   val cardCode: String = "",
                   @SerializedName("ExpirationMonth")
                   val expirationMonth: String = "",
                   @SerializedName("AccountNumber")
                   val accountNumber: String = "",
                   @SerializedName("ExpirationYear")
                   val expirationYear: String = "")