package com.paeparo.paeparo_mobile.model

data class Trip(
    var tripId: String = "",
    var name: String = "",
    var startDate: Long = 0L,
    var endDate: Long = 0L,
    var budget: Int = 0,
    var members: Map<String, Boolean> = mapOf(),
    var genderDistribution: GenderDistribution = GenderDistribution(),
    var ageDistribution: AgeDistribution = AgeDistribution(),
    var travelPreferences: TravelPreferences = TravelPreferences()
) {
    fun toMapWithoutTripId(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "start_date" to startDate,
            "end_date" to endDate,
            "budget" to budget,
            "members" to members,
            "gender_distribution" to genderDistribution,
            "age_distribution" to ageDistribution,
            "travel_preferences" to travelPreferences
        )
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