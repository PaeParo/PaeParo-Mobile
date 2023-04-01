package com.paeparo.paeparo_mobile.model

data class TripUpdate(
    var tripId: String = "",
    var tripUpdateInfo: TripUpdateInfo = TripUpdateInfo()
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
    var userId: String = "",
    var eventReference: String = "",
    var updateType: TripUpdate.UpdateType = TripUpdate.UpdateType.NONE,
    var timestamp: Long = 0L
)