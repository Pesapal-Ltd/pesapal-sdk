package com.pesapal.sdkdemo.utils

import com.pesapal.sdk.utils.PESAPALAPI3SDK
import com.pesapal.sdkdemo.profile.KeysSecret

object PrefUtil {
    var keotherCurrency = listOf(PESAPALAPI3SDK.CURRENCY_CODE_KES, PESAPALAPI3SDK.CURRENCY_CODE_USD)
    var ugotherCurrency = listOf(PESAPALAPI3SDK.CURRENCY_CODE_UGX, PESAPALAPI3SDK.CURRENCY_CODE_USD)
    var tzotherCurrency = listOf(PESAPALAPI3SDK.CURRENCY_CODE_TZS, PESAPALAPI3SDK.CURRENCY_CODE_USD)
    var countriesList = listOf("Kenya", "Uganda", "Tanzania")
    val demoKeys = listOf(
        KeysSecret("Kenya","qkio1BGGYAXTu2JOfm7XSXNruoZsrqEW","osGQ364R49cXKeOYSpaOnT++rHs="),
        KeysSecret("Uganda","TDpigBOOhs+zAl8cwH2Fl82jJGyD8xev","1KpqkfsMaihIcOlhnBo/gBZ5smw="),
        KeysSecret("Tanzania","ngW+UEcnDhltUc5fxPfrCD987xMh3Lx8","q27RChYs5UkypdcNYKzuUw460Dg="),
    )

    fun setData(p2: Int){
        PrefManager.setCountry(countriesList[p2])
        PrefManager.setConsumerKey(demoKeys[p2].key)
        PrefManager.setConsumerSecret(demoKeys[p2].secret)
    }
}