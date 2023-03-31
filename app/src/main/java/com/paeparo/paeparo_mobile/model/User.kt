package com.paeparo.paeparo_mobile.model

data class User(
    var userId: String = "",
    var nickname: String = "",
    var age: Int = 0,
    var gender: String = "",
    var travelStyle: List<String> = listOf(),
    var likedPosts: List<String> = listOf()
)