package com.paeparo.paeparo_mobile.model

import com.google.firebase.firestore.GeoPoint
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

open class Event(
    var eventId: String = "",
    var name: String = "",
    var type: EventType = EventType.NONE,
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var budget: Int = 0
) {
    enum class EventType {
        NONE,
        PLACE,
        MOVE,
        MEAL
    }

    open fun toMapWithoutEventId(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "type" to type,
            "start_time" to startTime,
            "end_time" to endTime,
            "budget" to budget
        )
    }


    fun getChangedFields(other: Event): Map<String, Any?> {
        return this::class.declaredMemberProperties
            .filter { it.isAccessible }
            .mapNotNull { prop ->
                @Suppress("UNCHECKED_CAST")
                val typedProp = prop as KProperty1<Event, *>
                val currentMemberValue = typedProp.get(this)
                val otherMemberValue = prop.get(other)

                if (currentMemberValue != otherMemberValue) {
                    prop.name to otherMemberValue
                } else {
                    null
                }
            }.toMap()
    }
}

data class PlaceEvent(
    var place: Place = Place(),
) : Event() {
    override fun toMapWithoutEventId(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "type" to type,
            "start_time" to startTime,
            "end_time" to endTime,
            "budget" to budget,
            "place" to place
        )
    }
}

data class MoveEvent(
    var mode: String = "",
    var origin: Place = Place(),
    var destination: Place = Place()
) : Event() {
    override fun toMapWithoutEventId(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "type" to type,
            "start_time" to startTime,
            "end_time" to endTime,
            "budget" to budget,
            "mode" to mode,
            "origin" to origin,
            "destination" to destination
        )
    }
}


data class Place(
    var name: String = "",
    var location: GeoPoint = GeoPoint(0.0, 0.0),
)
