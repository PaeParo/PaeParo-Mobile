package com.paeparo.paeparo_mobile.model

data class User(
    val nickname: String = "",
    val age: Int = 0,
    val gender: String = "",
    val travelStyle: List<String> = listOf(),
    val trips: List<String> = listOf(),
    val likedPosts: List<String> = listOf(),
    val commentedPosts: List<CommentedPost> = listOf()
) {
}

data class CommentedPost(
    val postId: String = "",
    val comments: List<String> = listOf()
)