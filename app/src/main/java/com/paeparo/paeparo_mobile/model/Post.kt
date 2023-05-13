package com.paeparo.paeparo_mobile.model

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil

data class Post(
    @SerializedName("post_id") var postId: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("user_id") var userId: String = "",
    @SerializedName("trip_id") var tripId: String = "",
    @SerializedName("created_at") var createdAt: Timestamp = Timestamp.now(),
    @SerializedName("likes") var likes: Int = 0,
    @SerializedName("tags") var tags: List<String> = listOf(),
    @SerializedName("images") var images: List<String> = listOf(),
    @SerializedName("author_review") var authorReview: AuthorReview = AuthorReview(),
) {
    fun toMapWithoutPostId(): Map<String, Any?> {
        val serializedMap = FirestoreNamingUtil.toSerializedMap(this)
        return serializedMap.filterKeys { it != "post_id" }
    }

    fun getChangedFields(other: Post): Map<String, Any?> {
        val serializedThis = FirestoreNamingUtil.toSerializedMap(this)
        val serializedOther = FirestoreNamingUtil.toSerializedMap(other)

        return serializedThis.filter { (key, value) ->
            serializedOther[key] != value
        }
    }
}

data class AuthorReview(
    @SerializedName("rating") var rating: Double = 0.0,
    @SerializedName("rating_detail") var ratingDetail: RatingDetail = RatingDetail()
)

data class RatingDetail(
    @SerializedName("food") var food: Double = 0.0,
    @SerializedName("accommodation") var accommodation: Double = 0.0,
    @SerializedName("transportation") var transportation: Double = 0.0,
    @SerializedName("sightseeing") var touristDestination: Double = 0.0,
    @SerializedName("hygiene") var hygiene: Double = 0.0,
    @SerializedName("activity") var activity: Double = 0.0
)