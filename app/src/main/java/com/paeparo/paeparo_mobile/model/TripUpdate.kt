package com.paeparo.paeparo_mobile.model

import com.google.gson.annotations.SerializedName

data class TripUpdate(
    @SerializedName("trip_id") var tripId: String = "",
    @SerializedName("trip_update_info") var tripUpdateInfo: TripUpdateInfo = TripUpdateInfo()
) {
    enum class UpdateType {
        NONE,
        CREATE,
        ADD,
        REMOVE,
        UPDATE
    }
}

data class TripUpdateInfo(
    @SerializedName("user_id") var userId: String = "",
    @SerializedName("event_reference") var eventReference: String = "",
    @SerializedName("update_type") var updateType: TripUpdate.UpdateType = TripUpdate.UpdateType.NONE,
    @SerializedName("timestamp") var timestamp: Long = 0L
)