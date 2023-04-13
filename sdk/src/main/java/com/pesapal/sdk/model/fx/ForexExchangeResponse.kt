package com.pesapal.sdk.model.fx

import com.google.gson.annotations.SerializedName

data class ForexExchangeResponse(@SerializedName("result")
                                 val result: Double = 0.0,
                                 @SerializedName("success")
                                 val success: Boolean = false,
                                 @SerializedName("query")
                                 val query: Query,
                                 @SerializedName("error")
                                 val error: Error? = null,
                                 @SerializedName("info")
                                 val info: Info)