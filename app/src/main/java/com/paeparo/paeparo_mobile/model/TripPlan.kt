package com.paeparo.paeparo_mobile.model

import com.paeparo.paeparo_mobile.manager.FirebaseManager
import java.util.Date

data class TripPlan (
    val trip: Trip,
    ){

    fun test(){
        trip.startDate
    }

    // 여행이 몇일인지 계산하는 함수
    fun getTripDays():Int{
        val diffMillis = trip.endDate.toDate().time - trip.startDate.toDate().time
        val diffDays = diffMillis / (1000 * 60 * 60 * 24)+1
        return diffDays.toInt()
    }

//    suspend fun groupEventsByDay(): Map<String, List<Event>> {
//        val list = FirebaseManager.getTripEvents(trip.tripId)
//        trip.startDate.
//    }

//    fun getDayOfMonth(date: Date): Int {
//        val cal = Calendar.getInstance()
//        cal.time = date
//        return cal.get(Calendar.DAY_OF_MONTH)
//    }


}