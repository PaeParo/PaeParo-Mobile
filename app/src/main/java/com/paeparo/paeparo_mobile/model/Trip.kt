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
        fun getChanges(currentData: Any, otherData: Any): Map<String, Any?> {
            return currentData::class.declaredMemberProperties
                .filter { it.isAccessible }
                .mapNotNull { prop ->
                    @Suppress("UNCHECKED_CAST")
                    val typedProp = prop as KProperty1<Any, *>
                    val currentMemberValue = typedProp.get(currentData)
                    val otherMemberValue = typedProp.get(otherData)

                    when {
                        currentMemberValue == otherMemberValue -> null
                        currentMemberValue is Any && currentMemberValue::class.isData -> {
                            val nestedChanges = getChanges(
                                currentMemberValue,
                                otherMemberValue ?: return@mapNotNull null
                            )
                            if (nestedChanges.isNotEmpty()) prop.name to nestedChanges else null
                        }
                        else -> prop.name to otherMemberValue
                    }
                }.toMap()
        }
        return getChanges(this, other)
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
    var activity: Int = 0,
    var food: Int = 0,
    var shopping: Int = 0,
    var culture: Int = 0,
    var sightseeing: Int = 0,
    var healing: Int = 0,
    var accommodation: Int = 0,
)