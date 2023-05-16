package com.paeparo.paeparo_mobile.model

import com.google.firebase.Timestamp
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.util.DateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date

data class TripPlan(
    val trip: Trip,
) {
    lateinit var map: Map<String, List<Event>>
    var eventList: List<Event>? = null

    lateinit var tripStartDate: Timestamp
    init {
        tripStartDate = trip.startDate
        val tripStart = DateUtil.getDateFromTimestamp(trip.startDate, DateUtil.yyyyMdFormat)
        Timber.d("Trip start : $tripStart")
        //getting Events
        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseManager.getTripEvents(trip.tripId)
            if (!result.isSuccess) {
                eventList = result.data
            }
        }
        eventList?.forEach { event ->
            val eventStart = DateUtil.getDateFromTimestamp(event.startTime, DateUtil.yyyyMdFormat)
            Timber.d("Event Start :${eventStart}")
        }

    }



}