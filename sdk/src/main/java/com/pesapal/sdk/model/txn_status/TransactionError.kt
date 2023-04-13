package com.pesapal.sdk.model.txn_status

import com.google.gson.annotations.SerializedName

data class TransactionError(@SerializedName("code")
                 val code: String = "",
                            @SerializedName("error_type")
                 val errorType: String = "",
                            @SerializedName("message")
                 val message: String = ""): java.io.Serializable