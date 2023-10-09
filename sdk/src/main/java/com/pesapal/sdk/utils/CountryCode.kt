package com.pesapal.sdk.utils

data class CountryCode(val mobileProvider: String, val paymentMethodId: Int, val countryCode: Int, var minimumAmount:Int = 1, var maximumAmount: Int = 0)

