package com.pesapal.paygateway.activities.payment.model.cacontinie

import com.google.gson.annotations.SerializedName

data class Consumer(@SerializedName("Account")
                    val account: Account)