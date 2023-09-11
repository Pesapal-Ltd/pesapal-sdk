package com.pesapal.sdk.model.card

import java.io.Serializable

internal data class CardDetails(
    var cardNumber: String = "",
    var year: Int = 2020,
    var month: Int = 10,
    var cvv: String = ""
): Serializable