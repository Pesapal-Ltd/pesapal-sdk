package com.pesapal.sdk.model.card

//data class CardinalRequest(
//    val authentication_transaction_id: String,
//    val card_details: CardDetailsX,
//    val order_tracking_id: String,
//    val specification_version: String,
//    val veres_enrolled: String
//)


internal data class CardinalRequest(
//    val authentication_transaction_id: String,
    val card_details: BillingAddress,
    val order_tracking_id: String,
//    val specification_version: String,
//    val veres_enrolled: String
)

data class CardDetailsX(
    val card_number: String,
    val cvv: String,
    val expiry_month: Int,
    val expiry_year: Int
)