package com.paeparo.paeparo_mobile.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil

data class LocationUpdate(
    @SerializedName("trip_id") var tripId: String = "",
    @SerializedName("member_locations") var memberLocations: List<LocationUpdateInfo> = listOf()
) {
    fun toMapWithoutTripId(): Map<String, Any?> {
        val serializedMap = FirestoreNamingUtil.toSerializedMap(this)
        return serializedMap.filterKeys { it != "trip_id" }
    }
}

data class LocationUpdateInfo(
    @SerializedName("user_id") var userId: String = "",
    @SerializedName("timestamp") var timestamp: Timestamp = Timestamp.now(),
    @SerializedName("location") var location: GeoPoint = GeoPoint(0.0, 0.0)
) {
    fun toMapWithouUserId(): Map<String, Any?> {
        val serializedMap = FirestoreNamingUtil.toSerializedMap(this)
        return serializedMap.filterKeys { it != "trip_id" }
    }
}