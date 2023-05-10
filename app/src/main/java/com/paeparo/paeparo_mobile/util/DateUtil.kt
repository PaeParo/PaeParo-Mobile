package com.paeparo.paeparo_mobile.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    val yyyyMMddFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    fun getDateFromLong(date: Long, format: SimpleDateFormat): String {
        return format.format(Date(date))
    }
}