package com.paeparo.paeparo_mobile.model

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil

data class Comment(
    @SerializedName("comment_id") var commentId: String = "",
    @SerializedName("post_id") var postId: String = "",
    @SerializedName("nickname") var nickname: String = "",
    @SerializedName("created_at") var createdAt: Timestamp = Timestamp.now(),
    @SerializedName("content") var content: String = ""
) {
    fun toMapWithoutCommentId(): Map<String, Any?> {
        val serializedMap = FirestoreNamingUtil.toSerializedMap(this)
        return serializedMap.filterKeys { it != "comment_id" }
    }

    fun getChangedFields(other: Comment): Map<String, Any?> {
        val currentMap = FirestoreNamingUtil.toSerializedMap(this)
        val otherMap = FirestoreNamingUtil.toSerializedMap(other)

        return currentMap.filterKeys { key ->
            otherMap[key] != currentMap[key]
        }.mapValues { entry ->
            otherMap[entry.key]
        }
    }
}