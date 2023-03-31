package com.paeparo.paeparo_mobile.model

data class TripUpdate(
    var tripId: String = "",
    var lastUpdate: TripUpdateInfo = TripUpdateInfo()
)

data class TripUpdateInfo(
    var userId: String = "",
    var eventId: String = "",
    var eventType: String = "",
    var timestamp: Long = 0L
)