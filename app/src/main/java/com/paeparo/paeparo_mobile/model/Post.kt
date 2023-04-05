package com.paeparo.paeparo_mobile.model

import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

data class Post(
    var postId: String = "",
    var title: String = "",
    var description: String = "",
    var userId: String = "",
    var tripId: String = "",
    var createdAt: Long = 0L,
    var likes: Int = 0,
    var tags: List<String> = listOf(),
    var images: List<String> = listOf(),
    var authorReview: AuthorReview = AuthorReview(),
) {
    fun getChangedFields(other: Post): Map<String, Any?> {
        return this::class.declaredMemberProperties
            .filter { it.isAccessible }
            .mapNotNull { prop ->
                @Suppress("UNCHECKED_CAST")
                val typedProp = prop as KProperty1<Post, *>
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

data class AuthorReview(
    var rating: Double = 0.0,
    var ratingDetail: RatingDetail = RatingDetail()
)

data class RatingDetail(
    var food: Double = 0.0,
    var accommodation: Double = 0.0,
    var transportation: Double = 0.0,
    var touristDestination: Double = 0.0,
    var hygiene: Double = 0.0,
    var activity: Double = 0.0
)