package com.paeparo.paeparo_mobile.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil

@IgnoreExtraProperties
data class Post(
    @get:Exclude @SerializedName("post_id") var postId: String = "",
    @set:PropertyName("title") @get:PropertyName("title") @SerializedName("title") var title: String = "",
    @set:PropertyName("description") @get:PropertyName("description") @SerializedName("description") var description: String = "",
    @set:PropertyName("user_id") @get:PropertyName("user_id") @SerializedName("user_id") var userId: String = "",
    @set:PropertyName("trip_id") @get:PropertyName("trip_id") @SerializedName("trip_id") var tripId: String = "",
    @set:PropertyName("created_at") @get:PropertyName("created_at") @SerializedName("created_at") var createdAt: Timestamp = Timestamp.now(),
    @set:PropertyName("likes") @get:PropertyName("likes") @SerializedName("likes") var likes: Int = 0,
    @set:PropertyName("comments") @get:PropertyName("comments") @SerializedName("comments") var comments: Int = 0,
    @set:PropertyName("tags") @get:PropertyName("tags") @SerializedName("tags") var tags: List<String> = listOf(),
    @set:PropertyName("images") @get:PropertyName("images") @SerializedName("images") var images: List<String> = listOf(),
    @set:PropertyName("author_review") @get:PropertyName("author_review") @SerializedName("author_review") var authorReview: AuthorReview = AuthorReview(),
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
    @set:PropertyName("rating") @get:PropertyName("rating") @SerializedName("rating") var rating: Double = 0.0,
    @set:PropertyName("rating_detail") @get:PropertyName("rating_detail") @SerializedName("rating_detail") var ratingDetail: RatingDetail = RatingDetail()
)

data class RatingDetail(
    @set:PropertyName("food") @get:PropertyName("food") @SerializedName("food") var food: Double = 0.0,
    @set:PropertyName("accommodation") @get:PropertyName("accommodation") @SerializedName("accommodation") var accommodation: Double = 0.0,
    @set:PropertyName("transportation") @get:PropertyName("transportation") @SerializedName("transportation") var transportation: Double = 0.0,
    @set:PropertyName("sightseeing") @get:PropertyName("sightseeing") @SerializedName("sightseeing") var touristDestination: Double = 0.0,
    @set:PropertyName("hygiene") @get:PropertyName("hygiene") @SerializedName("hygiene") var hygiene: Double = 0.0,
    @set:PropertyName("activity") @get:PropertyName("activity") @SerializedName("activity") var activity: Double = 0.0
)