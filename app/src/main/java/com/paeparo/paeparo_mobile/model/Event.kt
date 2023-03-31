package com.paeparo.paeparo_mobile.model

import com.google.firebase.firestore.GeoPoint

open class Event(
    var eventId: String = "",
    var name: String = "",
    var type: String = "",
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var budget: Int = 0
)
data class PlaceEvent(
    var place: Place = Place(),
) : Event()

data class MoveEvent(
    var mode: String = "",
    var origin: Place = Place(),
    var destination: Place = Place()
) : Event()


data class Place(
    var name: String = "",
    var location: GeoPoint = GeoPoint(0.0, 0.0),
)
