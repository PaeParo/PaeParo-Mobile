package com.paeparo.paeparo_mobile.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
open class Event(
    @get:Exclude @SerializedName("event_id") var eventId: String = "",
    @set:PropertyName("name") @get:PropertyName("name") @SerializedName("name") var name: String = "",
    @set:PropertyName("type") @get:PropertyName("type") @SerializedName("type") var type: EventType = EventType.NONE,
    @set:PropertyName("start_time") @get:PropertyName("start_time") @SerializedName("start_time") var startTime: Timestamp = Timestamp.now(),
    @set:PropertyName("end_time") @get:PropertyName("end_time") @SerializedName("end_time") var endTime: Timestamp = Timestamp.now(),
    @set:PropertyName("budget") @get:PropertyName("budget") @SerializedName("budget") var budget: Int = 0
):Parcelable {
    enum class EventType {
        NONE,
        PLACE,
        MOVE,
        MEAL
    }

    open fun cloneWith(startTime: Timestamp, endTime: Timestamp): Event {
        return Event(eventId, name, type, startTime, endTime, budget)
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

    override fun equals(other: Any?): Boolean {
        val otherEvent = other as Event
        return this.eventId == other.eventId
    }
}

data class PlaceEvent(
    @set:PropertyName("place") @get:PropertyName("place") @SerializedName("place") var place: Place = Place(),
) : Event() {
    override fun cloneWith(startTime: Timestamp, endTime: Timestamp): PlaceEvent {
        return PlaceEvent(place).also {
            it.name = name
            it.type = type
            it.startTime = startTime
            it.endTime = endTime
            it.budget = budget
            it.eventId = eventId
        }
    }
}

data class MoveEvent(
    @set:PropertyName("mode") @get:PropertyName("mode") @SerializedName("mode") var mode: String = "",
    @set:PropertyName("origin") @get:PropertyName("origin") @SerializedName("origin") var origin: Place = Place(),
    @set:PropertyName("destination") @get:PropertyName("destination") @SerializedName("destination") var destination: Place = Place()
) : Event() {
    override fun cloneWith(startTime: Timestamp, endTime: Timestamp): MoveEvent {
        return MoveEvent(mode, origin, destination).also {
            it.name = name
            it.type = type
            it.startTime = startTime
            it.endTime = endTime
            it.budget = budget
            it.eventId = eventId
        }
    }
}

data class Place(
    @set:PropertyName("name") @get:PropertyName("name") @SerializedName("name") var name: String = "",
    @set:PropertyName("location") @get:PropertyName("location") @SerializedName("location") var location: GeoPoint = GeoPoint(
        0.0,
        0.0
    ),
)