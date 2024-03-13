package com.pesapal.sdkdemo.utils

import com.pesapal.sdkdemo.profile.KeysSecret

object PrefUtil {
    var keotherCurrency = listOf("KES", "USD")
    var ugotherCurrency = listOf("UGX", "USD")
    var tzotherCurrency = listOf("TZS", "USD")
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