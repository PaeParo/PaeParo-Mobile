package com.paeparo.paeparo_mobile.model

data class Post(
    val postId: String = "",
    val title: String = "",
    val description: String = "",
    val userId: String = "",
    val tripId: String = "",
    val createdAt: String = "",
    val like: Int = 0,
    val images: List<String> = listOf(),
    val authorReview: AuthorReview = AuthorReview(),
    val comments: Map<String, Comment> = mapOf()
)

data class AuthorReview(
    val rating: Double = 0.0,
    val ratingDetail: RatingDetail = RatingDetail()
)

data class RatingDetail(
    val food: Double = 0.0,
    val accommodation: Double = 0.0,
    val transportation: Double = 0.0,
    val touristDestination: Double = 0.0,
    val hygiene: Double = 0.0,
    val activity: Double = 0.0
)

data class Comment(
    val userId: String = "",
    val nickname: String = "",
    val createdAt: String = "",
    val content: String = ""
)