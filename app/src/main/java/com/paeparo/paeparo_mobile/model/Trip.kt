package com.paeparo.paeparo_mobile.model

data class Trip(
    val tripId: String = "",
    val name: String = "",
    val duration: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val budget: Int = 0,
    val members: Map<String, Boolean> = mapOf(),
    val genderDistribution: GenderDistribution = GenderDistribution(),
    val ageDistribution: AgeDistribution = AgeDistribution(),
    val travelPreferences: TravelPreferences = TravelPreferences()
)
data class GenderDistribution(
    var male: Int = 0,
    var female: Int = 0
)

data class AgeDistribution(
    var `10s`: Int = 0,
    var `20s`: Int = 0,
    var `30s`: Int = 0,
    var `40s`: Int = 0,
    var `50s`: Int = 0,
    var `60s`: Int = 0
)

data class TravelPreferences(
    var activity: Int = 0,
    var scenery: Int = 0,
    var relaxation: Int = 0,
    var luxury: Int = 0,
    var costEffectiveness: Int = 0,
    var accommodation: Int = 0,
    var food: Int = 0,
    var transportation: Int = 0,
    var shopping: Int = 0
)