package com.paeparo.paeparo_mobile.model

import com.google.firebase.firestore.GeoPoint
import com.google.gson.annotations.SerializedName

data class LocationUpdate(
    @SerializedName("trip_id") var tripId: String = "",
    @SerializedName("member_locations") var memberLocations: List<LocationUpdateInfo> = listOf()
)

data class LocationUpdateInfo(
    @SerializedName("user_id") var userId: String = "",
    @SerializedName("timestamp") var timestamp: Long = 0L,
    @SerializedName("location") var location: GeoPoint = GeoPoint(0.0, 0.0)
)