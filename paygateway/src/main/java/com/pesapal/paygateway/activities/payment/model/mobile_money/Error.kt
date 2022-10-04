package com.pesapal.paygateway.activities.payment.model.mobile_money

import com.google.gson.annotations.SerializedName

data class Error(@SerializedName("code")
                 val code: String = "",
                 @SerializedName("error_type")
                 val errorType: String = "",
                 @SerializedName("message")
                 val message: String = "")