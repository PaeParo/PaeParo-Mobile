package com.paeparo.paeparo_mobile.model

data class Trip(
    val tripId: String = "",
    val name: String = "",
    val duration: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val budget: Int = 0,
    val members: List<String> = listOf(),
    val genderDistribution: GenderDistribution = GenderDistribution(),
    val ageDistribution: AgeDistribution = AgeDistribution(),
    val travelPreferences: TravelPreferences = TravelPreferences()
)

data class GenderDistribution(
    val male: Int = 0,
    val female: Int = 0
)

data class AgeDistribution(
    val `10s`: Int = 0,
    val `20s`: Int = 0,
    val `30s`: Int = 0,
    val `40s`: Int = 0,
    val `50s`: Int = 0,
    val `60s`: Int = 0
)

data class TravelPreferences(
    val activity: Int = 0,
    val scenery: Int = 0,
    val relaxation: Int = 0,
    val luxury: Int = 0,
    val costEffectiveness: Int = 0,
    val accommodation: Int = 0,
    val food: Int = 0,
    val transportation: Int = 0,
    val shopping: Int = 0
)