package com.paeparo.paeparo_mobile.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil

data class LocationUpdate(
    @set:PropertyName("trip_id") @get:PropertyName("trip_id") @SerializedName("trip_id") var tripId: String = "",
    @set:PropertyName("member_locations") @get:PropertyName("member_locations") @SerializedName("member_locations") var memberLocations: List<LocationUpdateInfo> = listOf()
) {
    fun toMapWithoutTripId(): Map<String, Any?> {
        val serializedMap = FirestoreNamingUtil.toSerializedMap(this)
        return serializedMap.filterKeys { it != "trip_id" }
    }
}

data class LocationUpdateInfo(
    @set:PropertyName("user_id") @get:PropertyName("user_id") @SerializedName("user_id") var userId: String = "",
    @set:PropertyName("timestamp") @get:PropertyName("timestamp") @SerializedName("timestamp") var timestamp: Timestamp = Timestamp.now(),
    @set:PropertyName("location") @get:PropertyName("location") @SerializedName("location") var location: GeoPoint = GeoPoint(
        0.0,
        0.0
    )
) {
    fun toMapWithouUserId(): Map<String, Any?> {
        val serializedMap = FirestoreNamingUtil.toSerializedMap(this)
        return serializedMap.filterKeys { it != "trip_id" }
    }
}