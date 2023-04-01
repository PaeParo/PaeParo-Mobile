package com.paeparo.paeparo_mobile.constant

object FirebaseConstants {
    enum class RegistrationStatus {
        NICKNAME_NOT_REGISTERED,
        DETAIL_INFO_NOT_REGISTERED,
        REGISTERED
    }

    enum class UpdateType {
        NONE,
        CREATE,
        ADD,
        REMOVE,
        UPDATE
    }

    enum class EventType {
        NONE,
        PLACE,
        MOVE,
        MEAL
    }
}