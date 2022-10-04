package com.pesapal.paygateway.activities.payment.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RequestMakeCard : Parcelable {
    var billingDetails: BillingDetails = BillingDetails()
    var name: String = ""
    var cardDetails: CardDetails = CardDetails()
}

@Parcelize
class BillingDetails : Parcelable {
    var postalCode: String = ""// "90108"
    var city: String = "" // "Kilimani"
    var email: String = ""// "jmasai@pesapal.com"
    var street: String = ""//""Job"
    var firstName: String = ""// "Masai"
    var lastName: String = ""// "Masai"
    var msisdn: String = ""// "0712345678"
    var countryCode: String = ""//""KEN"
    var country: String = ""//"Kenya"
    var state: String = ""
}

@Parcelize
class CardDetails : Parcelable {
    var cardNumber: String = ""// "4761101492527947"
    var year: Int = 2020 //TODO set default to current year
    var month: Int = 10 //TODO set default to current month
    var cvv: String = ""// "123"
}
