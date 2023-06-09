package com.pesapal.sdk.model.server_jwt

import com.google.gson.annotations.SerializedName
import com.pesapal.sdk.model.card.BillingAddress
import java.math.BigDecimal

data class RequestServerJwt(@SerializedName("amount")
                            val amount: BigDecimal = BigDecimal.ZERO,
                            @SerializedName("currency")
                            val currency: String = "",
                            @SerializedName("billing_address")
                            val billingAddress: BillingAddress,
                            @SerializedName("card_details")
                            val cardDetails: CardDetails,
                            )