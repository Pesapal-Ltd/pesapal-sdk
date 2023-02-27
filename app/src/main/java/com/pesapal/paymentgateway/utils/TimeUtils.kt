package com.pesapal.paymentgateway.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    fun getCurrentDateTime(): String? {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val cal = Calendar.getInstance()
        return dateFormat.format(cal.time)
    }

}