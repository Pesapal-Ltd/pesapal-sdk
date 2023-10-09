package com.pesapal.sdk.utils

object CountryCodeEval {

    const val MPESA     = 1
    const val AIRTEL_KE = 2

    const val MTN_UG    = 3
    const val AIRTEL_UG = 8

    const val TIGO_TANZANIA = 4
    const val MPESA_TZ  = 5
    const val AIRTEL_TZ = 9

    const val CARD = 100

    const val KE_COUNTRY_CODE = 254
    private const val TZ_COUNTRY_CODE = 255
    private const val UG_COUNTRY_CODE = 256

    private const val MPESA_PROV_NAME  = "Mpesa"
    private const val AIRTEL_PROV_NAME = "Airtel"
    private const val MTN_PROV_NAME    = "MTN"
    private const val TIGO_PROV_NAME   = "Tigo"



    val mappingAllCountries = hashMapOf(
        MPESA     to     CountryCode(MPESA_PROV_NAME  , MPESA, KE_COUNTRY_CODE),
        AIRTEL_KE to     CountryCode(AIRTEL_PROV_NAME , AIRTEL_KE, KE_COUNTRY_CODE),
        MTN_UG    to     CountryCode(MTN_PROV_NAME    , MTN_UG, UG_COUNTRY_CODE,    minimumAmount = 500),
        AIRTEL_UG to     CountryCode(AIRTEL_PROV_NAME , AIRTEL_UG, UG_COUNTRY_CODE, minimumAmount = 50),
        TIGO_TANZANIA to CountryCode(TIGO_PROV_NAME   , TIGO_TANZANIA, TZ_COUNTRY_CODE),
        MPESA_TZ  to     CountryCode(MPESA_PROV_NAME  , MPESA_TZ, TZ_COUNTRY_CODE),
        AIRTEL_TZ to     CountryCode(AIRTEL_PROV_NAME , AIRTEL_TZ, TZ_COUNTRY_CODE),
    )


    val kenyaProvider    = listOf(MPESA, AIRTEL_KE)

    val ugandaProvider   = listOf(MTN_UG, AIRTEL_UG)

    val tanzaniaProvider = listOf(MPESA_TZ, TIGO_TANZANIA, AIRTEL_TZ)

}