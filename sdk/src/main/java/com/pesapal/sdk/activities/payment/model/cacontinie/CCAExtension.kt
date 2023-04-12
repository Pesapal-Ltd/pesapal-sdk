package com.pesapal.sdk.activities.payment.model.cacontinie

import com.google.gson.annotations.SerializedName

data class CCAExtension(@SerializedName("MerchantName")
                        val merchantName: String = "")