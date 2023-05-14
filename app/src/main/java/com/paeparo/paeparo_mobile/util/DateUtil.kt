package com.paeparo.paeparo_mobile.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    val yyyyMdFormat = SimpleDateFormat("yyyy.M.d", Locale.KOREA)

    fun getDateFromTimestamp(timestamp: Timestamp, format: SimpleDateFormat): String {
        return format.format(Date(timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000))
    }
}