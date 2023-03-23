package com.paeparo.paeparo_mobile.model

open class Event(
    val eventId: String = "",
    val name: String = "",
    val type: String = "",
    val startTime: String = "",
    val endTime: String = ""
)

data class PlaceEvent(
    val location: Place = Place(),
    val transportation: String = "",
) : Event("", "", "", "", "")

data class Transportation(
    val mode: String = "",
    val transitCount: Int = 0,
    val travelTime: Int = 0,
    val origin: Place = Place(),
    val destination: Place = Place()
) : Event("", "", "", "", "")

data class Place(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)