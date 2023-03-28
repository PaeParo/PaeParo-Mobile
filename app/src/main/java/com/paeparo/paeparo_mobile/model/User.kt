package com.paeparo.paeparo_mobile.model

data class User(
    val nickname: String = "",
    val age: Int = 0,
    val gender: String = "",
    val travelStyle: String = "",
    val trips: List<String> = listOf(),
    val likedPosts: List<String> = listOf(),
    val commentedPosts: List<CommentedPost> = listOf()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nickname" to nickname,
            "age" to age,
            "gender" to gender,
            "travelStyle" to travelStyle,
            "trips" to trips,
            "likedPosts" to likedPosts,
            "commentedPosts" to commentedPosts
        )
    }
}

data class CommentedPost(
    val postId: String = "",
    val comments: List<String> = listOf()
)