package com.pesapal.sdk.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object TimeUtils {

    val format1 = "EEE, dd MMMM yyyy HH:mm a"
    val format2 = "yyyy/MM/dd HH:mm:ss"

    fun getCurrentDateTime(): String? {
        val dateFormat = SimpleDateFormat(format1, Locale.getDefault())
        val cal = Calendar.getInstance()
        return dateFormat.format(cal.time)
    }

    fun getCurrentDateTime(txnTime: String):String{
        var reformatted = ""
        try{
            val firstApiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",  Locale.getDefault())
            val date = firstApiFormat.parse(txnTime)

            val dateFormat = SimpleDateFormat(format1, Locale.getDefault())
             reformatted = dateFormat.format(date!!)
        }
        catch (_: Exception){

        }
        return reformatted

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val d = "2024-03-06T14:41:08.117"
//            val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
//            val date = LocalDate.parse(d, firstApiFormat)
//
//            val dateFormat = SimpleDateFormat(format1, Locale.getDefault())
//            val cal = Calendar.getInstance()
//            val reformatted = dateFormat.format(cal.time)
//        }


    }

//Mon, 14 August 2023 14:38 PM

}