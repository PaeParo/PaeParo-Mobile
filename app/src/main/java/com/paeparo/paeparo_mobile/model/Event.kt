package com.paeparo.paeparo_mobile.model

import com.google.firebase.firestore.GeoPoint
import com.paeparo.paeparo_mobile.constant.FirebaseConstants

open class Event(
    var eventId: String = "",
    var name: String = "",
    var type: FirebaseConstants.EventType = FirebaseConstants.EventType.NONE,
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var budget: Int = 0
) {
    open fun toMapWithoutEventId(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "type" to type,
            "start_time" to startTime,
            "end_time" to endTime,
            "budget" to budget
        )
    }
}

data class PlaceEvent(
    var place: Place = Place(),
) : Event() {
    override fun toMapWithoutEventId(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "type" to type,
            "start_time" to startTime,
            "end_time" to endTime,
            "budget" to budget,
            "place" to place
        )
    }
}

data class MoveEvent(
    var mode: String = "",
    var origin: Place = Place(),
    var destination: Place = Place()
) : Event() {
    override fun toMapWithoutEventId(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "type" to type,
            "start_time" to startTime,
            "end_time" to endTime,
            "budget" to budget,
            "mode" to mode,
            "origin" to origin,
            "destination" to destination
        )
    }
}


data class Place(
    var name: String = "",
    var location: GeoPoint = GeoPoint(0.0, 0.0),
)
