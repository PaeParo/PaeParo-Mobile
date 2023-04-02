package com.paeparo.paeparo_mobile.model

import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

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

    fun getChangedFields(other: User): Map<String, Any?> {
        return this::class.declaredMemberProperties
            .filter { it.isAccessible }
            .mapNotNull { prop ->
                @Suppress("UNCHECKED_CAST")
                val typedProp = prop as KProperty1<User, *>
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