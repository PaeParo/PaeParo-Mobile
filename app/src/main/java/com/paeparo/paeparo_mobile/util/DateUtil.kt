package com.paeparo.paeparo_mobile.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
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
        return format.format(Date(timestamp.toMillis()))
    }

    /**
    TimeStamp의 값을 밀리세컨드로 변환
     */
    fun Timestamp.toMillis(): Long = (this.seconds * 1000 + this.nanoseconds / 1_000_000)

    fun Timestamp.toLocalDate(): LocalDate = this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

    /**
     * 두 LocalData간 기간 비교
     *
     * @param this 기간 종료
     * @param other 기간 시작
     * @return this - to
     */
    fun LocalDate.calPeriod(other: LocalDate): Int {
        val daysBetween = kotlin.math.abs(this.toEpochDay() - other.toEpochDay())
        return daysBetween.toInt()+1 // 여행 당일 포함 +1
    }
    // LocalDate를 Timestamp로 변환합니다.
    fun LocalDate.toTimestamp(): Timestamp {
        val instant = this.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val seconds = instant.epochSecond
        val nanos = instant.nano
        return Timestamp(seconds, nanos)
    }

}
