package com.paeparo.paeparo_mobile.model

data class User(
    var userId: String = "",
    var nickname: String = "",
    var age: Int = 0,
    var gender: String = "",
    var travelStyle: List<String> = listOf(),
    var likedPosts: List<String> = listOf()
) {
    fun toMapWithoutUserId(): Map<String, Any> {
        return mapOf(
            "nickname" to nickname,
            "age" to age,
            "gender" to gender,
            "travel_style" to travelStyle,
            "liked_posts" to likedPosts,
        )
    }
}