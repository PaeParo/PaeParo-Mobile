package com.paeparo.paeparo_mobile.model

import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

data class Trip(
    var tripId: String = "",
    var name: String = "",
    var status: TripStatus = TripStatus.NONE,
    var startDate: Long = 0L,
    var endDate: Long = 0L,
    var budget: Int = 0,
    var members: Map<String, Boolean> = mapOf(),
    var travelStyles: Map<String, List<String>> = mutableMapOf(),
    var genderDistribution: GenderDistribution = GenderDistribution(),
    var ageDistribution: AgeDistribution = AgeDistribution(),
    var travelPreferences: TravelPreferences = TravelPreferences()
) {
    enum class TripStatus {
        NONE,
        PLANNING,
        ONGOING,
        FINISHED
    }

    fun toMapWithoutTripId(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "status" to status,
            "start_date" to startDate,
            "end_date" to endDate,
            "budget" to budget,
            "members" to members,
            "travel_styles" to travelStyles,
            "gender_distribution" to genderDistribution,
            "age_distribution" to ageDistribution,
            "travel_preferences" to travelPreferences
        )
    }

    fun getChangedFields(other: Trip): Map<String, Any?> {
        return this::class.declaredMemberProperties
            .filter { it.isAccessible }
            .mapNotNull { prop ->
                @Suppress("UNCHECKED_CAST")
                val typedProp = prop as KProperty1<Trip, *>
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

data class GenderDistribution(
    var male: Int = 0,
    var female: Int = 0
)

data class AgeDistribution(
    var _10s: Int = 0,
    var _20s: Int = 0,
    var _30s: Int = 0,
    var _40s: Int = 0,
    var _50s: Int = 0,
    var _60s: Int = 0
)

data class TravelPreferences(
    var food: Int = 0,
    var complex: Int = 0,
    var activity: Int = 0
)