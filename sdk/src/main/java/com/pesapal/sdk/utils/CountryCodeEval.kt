package com.pesapal.sdk.utils

object CountryCodeEval {

    const val MPESA     = 1
    const val AIRTEL_KE = 2

    const val MTN_UG    = 3
    const val AIRTEL_UG = 8

    const val TIGO_TANZANIA = 4
    const val MPESA_TZ  = 5
    const val AIRTEL_TZ = 9

    private const val MPESA_PROV_NAME  = "Mpesa"
    private const val AIRTEL_PROV_NAME = "Airtel"
    private const val MTN_PROV_NAME    = "MTN"
    private const val TIGO_PROV_NAME   = "Tigo"

    val mapping = hashMapOf(
        MPESA     to MPESA_PROV_NAME,
        AIRTEL_KE to AIRTEL_PROV_NAME,
        MTN_UG    to MTN_PROV_NAME,
        AIRTEL_UG to AIRTEL_PROV_NAME,
        TIGO_TANZANIA to TIGO_PROV_NAME,
        MPESA_TZ  to MPESA_PROV_NAME,
        AIRTEL_TZ to AIRTEL_PROV_NAME,
    )

    // todo use a set to ensure items added are unique
    val kenyaProvider = listOf(
        CountryCode(MPESA_PROV_NAME  ,MPESA),
        CountryCode(AIRTEL_PROV_NAME , AIRTEL_KE)
    )

    val ugandaProvider = listOf(
        CountryCode(MTN_PROV_NAME    , MTN_UG),
        CountryCode(AIRTEL_PROV_NAME , AIRTEL_UG)
    )

    val tanzaniaProvider = listOf(
        CountryCode(MPESA_PROV_NAME  , MPESA_TZ),
        CountryCode(TIGO_PROV_NAME   , TIGO_TANZANIA),
        CountryCode(AIRTEL_PROV_NAME , AIRTEL_TZ)
    )

}