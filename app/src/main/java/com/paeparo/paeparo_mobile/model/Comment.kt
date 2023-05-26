package com.paeparo.paeparo_mobile.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil

@IgnoreExtraProperties
data class Comment(
    @get:Exclude @SerializedName("comment_id") var commentId: String = "",
    @set:PropertyName("post_id") @get:PropertyName("post_id") @SerializedName("post_id") var postId: String = "",
    @set:PropertyName("user_id") @get:PropertyName("user_id") @SerializedName("user_id") var userId: String = "",
    @set:PropertyName("nickname") @get:PropertyName("nickname") @SerializedName("nickname") var nickname: String = "",
    @set:PropertyName("created_at") @get:PropertyName("created_at") @SerializedName("created_at") var createdAt: Timestamp = Timestamp.now(),
    @set:PropertyName("user_thumbnail") @get:PropertyName("user_thumbnail") @SerializedName("user_thumbnail") var userThumbnail: String = "",
    @set:PropertyName("content") @get:PropertyName("content") @SerializedName("content") var content: String = ""
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