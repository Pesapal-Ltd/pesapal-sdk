package com.pesapal.sdk.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    val format1 = "EEE, dd MMMM yyyy HH:mm a"
    val format2 = "yyyy/MM/dd HH:mm:ss"

    fun getCurrentDateTime(): String? {
        val dateFormat = SimpleDateFormat(format1, Locale.getDefault())
        val cal = Calendar.getInstance()
        return dateFormat.format(cal.time)
    }

//Mon, 14 August 2023 14:38 PM

}