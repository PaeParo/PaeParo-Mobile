package com.paeparo.paeparo_mobile.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    /*
    계산 참고:
        계산방법 1970년 1월 1일 00:00:00 UTC + 이후의 지난 시간 초
        nanoseconds ( 1/1_000_000_000 sec)  | 1/1_000_000 milliseconds
        milliseconds ( 1/1000 sec )         | 1
        seconds ( 1초)                      | 1000 sec
     */
    val yyyyMdFormat = SimpleDateFormat("yyyy.M.d", Locale.KOREA)

    fun getDateFromTimestamp(timestamp: Timestamp, format: SimpleDateFormat): String {
        return format.format(Date(timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000))
    }

    infix fun Timestamp.isSameDate(that: Timestamp): Boolean =
        getDateFromTimestamp(this, yyyyMdFormat) == getDateFromTimestamp(that, yyyyMdFormat)
}
