package com.paeparo.paeparo_mobile.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    @set:PropertyName("trip_id") @get:PropertyName("trip_id") @SerializedName("trip_id") var tripId: String = "",
    @set:PropertyName("name") @get:PropertyName("name") @SerializedName("name") var name: String = "",
    @set:PropertyName("region") @get:PropertyName("region") @SerializedName("region") var region: String = "",
    @set:PropertyName("status") @get:PropertyName("status") @SerializedName("status") var status: TripStatus = TripStatus.NONE,
    @set:PropertyName("start_date") @get:PropertyName("start_date") @SerializedName("start_date") var startDate: Timestamp = Timestamp.now(),
    @set:PropertyName("end_date") @get:PropertyName("end_date") @SerializedName("end_date") var endDate: Timestamp = Timestamp.now(),
    @set:PropertyName("budget") @get:PropertyName("budget") @SerializedName("budget") var budget: Int = 0,
    @set:PropertyName("members") @get:PropertyName("members") @SerializedName("members") var members: List<String> = listOf(),
    @set:PropertyName("invitations") @get:PropertyName("invitations") @SerializedName("invitations") var invitations: List<String> = listOf(),
    @set:PropertyName("travel_styles") @get:PropertyName("travel_styles") @SerializedName("travel_styles") var travelStyles: Map<String, List<String>> = mutableMapOf(),
    @set:PropertyName("gender_distribution") @get:PropertyName("gender_distribution") @SerializedName(
        "gender_distribution"
    ) var genderDistribution: GenderDistribution = GenderDistribution(),
    @set:PropertyName("age_distribution") @get:PropertyName("age_distribution") @SerializedName("age_distribution") var ageDistribution: AgeDistribution = AgeDistribution(),
    @set:PropertyName("travel_preferences") @get:PropertyName("travel_preferences") @SerializedName(
        "travel_preferences"
    ) var travelPreferences: TravelPreferences = TravelPreferences()
) : Parcelable {
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

@Parcelize
data class GenderDistribution(
    @set:PropertyName("male") @get:PropertyName("male") @SerializedName("male") var male: Int = 0,
    @set:PropertyName("female") @get:PropertyName("female") @SerializedName("female") var female: Int = 0
) : Parcelable

@Parcelize
data class AgeDistribution(
    @set:PropertyName("10s") @get:PropertyName("10s") @SerializedName("10s") var _10s: Int = 0,
    @set:PropertyName("20s") @get:PropertyName("20s") @SerializedName("20s") var _20s: Int = 0,
    @set:PropertyName("30s") @get:PropertyName("30s") @SerializedName("30s") var _30s: Int = 0,
    @set:PropertyName("40s") @get:PropertyName("40s") @SerializedName("40s") var _40s: Int = 0,
    @set:PropertyName("50s") @get:PropertyName("50s") @SerializedName("50s") var _50s: Int = 0,
    @set:PropertyName("60s") @get:PropertyName("60s") @SerializedName("60s") var _60s: Int = 0
) : Parcelable

@Parcelize
data class TravelPreferences(
    @set:PropertyName("activity") @get:PropertyName("activity") @SerializedName("activity") var activity: Int = 0,
    @set:PropertyName("food") @get:PropertyName("food") @SerializedName("food") var food: Int = 0,
    @set:PropertyName("shopping") @get:PropertyName("shopping") @SerializedName("shopping") var shopping: Int = 0,
    @set:PropertyName("culture") @get:PropertyName("culture") @SerializedName("culture") var culture: Int = 0,
    @set:PropertyName("sightseeing") @get:PropertyName("sightseeing") @SerializedName("sightseeing") var sightseeing: Int = 0,
    @set:PropertyName("healing") @get:PropertyName("healing") @SerializedName("healing") var healing: Int = 0,
    @set:PropertyName("accommodation") @get:PropertyName("accommodation") @SerializedName("accommodation") var accommodation: Int = 0,
) : Parcelable
