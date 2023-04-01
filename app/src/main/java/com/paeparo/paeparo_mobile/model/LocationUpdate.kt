package com.paeparo.paeparo_mobile.model

import com.google.firebase.firestore.GeoPoint

data class LocationUpdate(
    var tripId: String = "",
    var memberLocations: List<LocationUpdateInfo> = listOf()
)

data class LocationUpdateInfo(
    var userId: String = "",
    var timestamp: Long = 0L,
    var location: GeoPoint = GeoPoint(0.0, 0.0)
)