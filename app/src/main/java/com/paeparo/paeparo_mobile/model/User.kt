package com.paeparo.paeparo_mobile.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import com.paeparo.paeparo_mobile.util.FirestoreNamingUtil

@IgnoreExtraProperties
data class User(
    @get:Exclude @SerializedName("user_id") var userId: String = "",
    @set:PropertyName("nickname") @get:PropertyName("nickname") @SerializedName("nickname") var nickname: String = "",
    @set:PropertyName("thumbnail") @get:PropertyName("thumbnail") @SerializedName("thumbnail") var thumbnail: String = "",
    @set:PropertyName("age") @get:PropertyName("age") @SerializedName("age") var age: Int = 0,
    @set:PropertyName("gender") @get:PropertyName("gender") @SerializedName("gender") var gender: String = "",
    @set:PropertyName("travel_style") @get:PropertyName("travel_style") @SerializedName("travel_style") var travelStyle: List<String> = listOf(),
    @set:PropertyName("liked_posts") @get:PropertyName("liked_posts") @SerializedName("liked_posts") var likedPosts: List<String> = listOf()
) {
    fun toMapWithoutUserId(): Map<String, Any?> {
        val serializedMap = FirestoreNamingUtil.toSerializedMap(this)
        return serializedMap.filterKeys { it != "user_id" }
    }

    fun getChangedFields(other: Event): Map<String, Any?> {
        val serializedThis = FirestoreNamingUtil.toSerializedMap(this)
        val serializedOther = FirestoreNamingUtil.toSerializedMap(other)

        return serializedThis.filter { (key, value) ->
            serializedOther[key] != value
        }
    }
}