package com.pesapal.sdk.activities.payment.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    fun getCurrentDateTime(): String? {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val cal = Calendar.getInstance()
        return dateFormat.format(cal.time)
    }

    fun getIntoMiliseconds(myDate: String): Long? {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = sdf.parse(myDate)

        return date.time
    }


}