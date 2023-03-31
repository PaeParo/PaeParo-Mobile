package com.paeparo.paeparo_mobile.model

data class Post(
    var postId: String = "",
    var title: String = "",
    var description: String = "",
    var userId: String = "",
    var tripId: String = "",
    var createdAt: Long = 0L,
    var like: Int = 0,
    var tags: List<String> = listOf(),
    var images: List<String> = listOf(),
    var authorReview: AuthorReview = AuthorReview(),
)

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