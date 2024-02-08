package com.pesapal.sdk.model.card

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

internal data class RequestServerJwt(@SerializedName("amount")
                            val amount: BigDecimal = BigDecimal.ZERO,
                            @SerializedName("currency")
                            val currency: String = "",
                            @SerializedName("billing_address")
                            val billingAddress: BillingAddress,
                            @SerializedName("card_details")
                            val cardDetails: CardDetails,
                            )