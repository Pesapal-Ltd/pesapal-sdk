package com.pesapal.sdk.utils

import java.text.DecimalFormat

object GeneralUtil {


    fun formatAmountText(amount: Double): String {
        val format = DecimalFormat("#,###,###.00")
        var displayText = "0.00"
        try{
            if (amount > 1)
                displayText = format.format(amount)
        }
        catch (ex:Exception){
        }

        return displayText

    }

}