package com.pesapal.paygateway.activities.payment.model.card

data class CardDetails(
    var cardNumber: String = "",
    var year: Int = 2020,
    var month: Int = 10,
    var cvv: String = ""
)