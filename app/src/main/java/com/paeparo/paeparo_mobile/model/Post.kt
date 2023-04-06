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