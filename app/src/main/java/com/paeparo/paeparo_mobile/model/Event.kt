package com.paeparo.paeparo_mobile.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil

open class Event(
    @SerializedName("event_id") var eventId: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("type") var type: EventType = EventType.NONE,
    @SerializedName("start_time") var startTime: Timestamp = Timestamp.now(),
    @SerializedName("end_time") var endTime: Timestamp = Timestamp.now(),
    @SerializedName("budget") var budget: Int = 0
) {
    enum class EventType {
        NONE,
        PLACE,
        MOVE,
        MEAL
    }
    fun toMapWithoutEventId(): Map<String, Any?> {
        val serializedMap = FirestoreNamingUtil.toSerializedMap(this)
        return serializedMap.filterKeys { it != "event_id" }
    }

    fun getChangedFields(other: Event): Map<String, Any?> {
        val serializedThis = FirestoreNamingUtil.toSerializedMap(this)
        val serializedOther = FirestoreNamingUtil.toSerializedMap(other)

        return serializedThis.filter { (key, value) ->
            serializedOther[key] != value
        }
    }
}

data class PlaceEvent(
    @SerializedName("place") var place: Place = Place(),
) : Event()

data class MoveEvent(
    @SerializedName("mode") var mode: String = "",
    @SerializedName("origin") var origin: Place = Place(),
    @SerializedName("destination") var destination: Place = Place()
) : Event()


data class Place(
    @SerializedName("name") var name: String = "",
    @SerializedName("location") var location: GeoPoint = GeoPoint(0.0, 0.0),
)
