package com.paeparo.paeparo_mobile.model

import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

data class Comment(
    var commentId: String = "",
    var postId: String = "",
    var nickname: String = "",
    var createdAt: Long = 0L,
    var content: String = ""
) {
    fun getChangedFields(other: Comment): Map<String, Any?> {
        return this::class.declaredMemberProperties
            .filter { it.isAccessible }
            .mapNotNull { prop ->
                @Suppress("UNCHECKED_CAST")
                val typedProp = prop as KProperty1<Comment, *>
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