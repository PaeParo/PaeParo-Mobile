package com.paeparo.paeparo_mobile.model

import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil

data class Trip(
    @SerializedName("trip_id") var tripId: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("region") var region: String = "",
    @SerializedName("status") var status: TripStatus = TripStatus.NONE,
    @SerializedName("start_date") var startDate: Long = 0L,
    @SerializedName("end_date") var endDate: Long = 0L,
    @SerializedName("budget") var budget: Int = 0,
    @SerializedName("members") var members: List<String> = listOf(),
    @SerializedName("invitations") var invitations: List<String> = listOf(),
    @SerializedName("travel_styles") var travelStyles: Map<String, List<String>> = mutableMapOf(),
    @SerializedName("gender_distribution") var genderDistribution: GenderDistribution = GenderDistribution(),
    @SerializedName("age_distribution") var ageDistribution: AgeDistribution = AgeDistribution(),
    @SerializedName("travel_preferences") var travelPreferences: TravelPreferences = TravelPreferences()
) {
    enum class TripStatus {
        NONE,
        PLANNING,
        ONGOING,
        FINISHED
    }

    fun toMapWithoutTripId(): Map<String, Any?> {
        val serializedMap = FirestoreNamingUtil.toSerializedMap(this)
        return serializedMap.filterKeys { it != "trip_id" }
    }

    fun getChangedFields(other: Trip): Map<String, Any?> {
        val serializedThis = FirestoreNamingUtil.toSerializedMap(this)
        val serializedOther = FirestoreNamingUtil.toSerializedMap(other)

        return serializedThis.filter { (key, value) ->
           serializedOther[key] != value
        }
    }
}

data class GenderDistribution(
    @SerializedName("male") var male: Int = 0,
    @SerializedName("female") var female: Int = 0
)

data class AgeDistribution(
    @SerializedName("10s") var _10s: Int = 0,
    @SerializedName("20s") var _20s: Int = 0,
    @SerializedName("30s") var _30s: Int = 0,
    @SerializedName("40s") var _40s: Int = 0,
    @SerializedName("50s") var _50s: Int = 0,
    @SerializedName("60s") var _60s: Int = 0
)

data class TravelPreferences(
    @SerializedName("activity") var activity: Int = 0,
    @SerializedName("food") var food: Int = 0,
    @SerializedName("shopping") var shopping: Int = 0,
    @SerializedName("culture") var culture: Int = 0,
    @SerializedName("sightseeing") var sightseeing: Int = 0,
    @SerializedName("healing") var healing: Int = 0,
    @SerializedName("accommodation") var accommodation: Int = 0,
)