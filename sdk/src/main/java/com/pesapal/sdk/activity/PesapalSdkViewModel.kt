package com.pesapal.sdk.activity

import androidx.lifecycle.ViewModel
import com.pesapal.sdk.model.card.BillingAddress
import com.pesapal.sdk.model.payment.PaymentDetails

internal class PesapalSdkViewModel : ViewModel() {
    var billingAddress: BillingAddress? = null
    var paymentDetails: PaymentDetails? = null
    var merchantName: String? = ""
    var trackingId: String? = ""
    var orderID:String? = ""
}