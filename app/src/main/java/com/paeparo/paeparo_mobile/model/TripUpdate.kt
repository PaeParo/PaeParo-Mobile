package com.paeparo.paeparo_mobile.model

import com.paeparo.paeparo_mobile.constant.FirebaseConstants

data class TripUpdate(
    var tripId: String = "",
    var tripUpdateInfo: TripUpdateInfo = TripUpdateInfo()
)

data class TripUpdateInfo(
    var userId: String = "",
    var eventReference: String = "",
    var updateType: FirebaseConstants.UpdateType = FirebaseConstants.UpdateType.NONE,
    var timestamp: Long = 0L
)