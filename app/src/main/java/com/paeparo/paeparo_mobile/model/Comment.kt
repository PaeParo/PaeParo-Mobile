package com.paeparo.paeparo_mobile.model

data class Comment(
    var commentId: String = "",
    var nickname: String = "",
    var createdAt: Long = 0L,
    var content: String = ""
)