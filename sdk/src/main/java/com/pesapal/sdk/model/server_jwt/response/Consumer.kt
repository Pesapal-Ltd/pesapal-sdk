package com.pesapal.sdk.model.server_jwt.response

import com.google.gson.annotations.SerializedName

data class Consumer(@SerializedName("Email1")
                    val email: String = "")