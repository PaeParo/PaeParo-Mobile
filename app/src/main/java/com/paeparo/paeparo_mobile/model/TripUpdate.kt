package com.paeparo.paeparo_mobile.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName

data class TripUpdate(
    @set:PropertyName("trip_id") @get:PropertyName("trip_id") @SerializedName("trip_id") var tripId: String = "",
    @set:PropertyName("trip_update_info") @get:PropertyName("trip_update_info") @SerializedName("trip_update_info") var tripUpdateInfo: TripUpdateInfo = TripUpdateInfo()
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
    @set:PropertyName("user_id") @get:PropertyName("user_id") @SerializedName("user_id") var userId: String = "",
    @set:PropertyName("event_reference") @get:PropertyName("event_reference") @SerializedName("event_reference") var eventReference: String = "",
    @set:PropertyName("update_type") @get:PropertyName("update_type") @SerializedName("update_type") var updateType: TripUpdate.UpdateType = TripUpdate.UpdateType.NONE,
    @set:PropertyName("timestamp") @get:PropertyName("timestamp") @SerializedName("timestamp") var timestamp: Timestamp = Timestamp.now()
)