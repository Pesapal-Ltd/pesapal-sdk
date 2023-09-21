package com.pesapal.sdk.utils

object CountryCodeEval {

    var MPESA = 1
    var AIRTEL_KE = 2

    var MTN_UG = 3
    var AIRTEL_UG = 8

    var TIGO_TANZANIA = 4
    var MPESA_TZ = 5
    var AIRTEL_TZ = 9

    // todo use a set to ensure items added are unique
    private val kenyaProvider = listOf(
        CountryCode("Mpesa"  ,MPESA),
        CountryCode("Airtel" , AIRTEL_KE)
    )

    private val ugandaProvider = listOf(
        CountryCode("MTN"  , MTN_UG),
        CountryCode("Airtel" , AIRTEL_UG)
    )

    private val tanzaniaProvider = listOf(
        CountryCode("Mpesa"  , MPESA_TZ),
        CountryCode("Tigo" , TIGO_TANZANIA),
        CountryCode("Airel" , AIRTEL_TZ)
    )

}