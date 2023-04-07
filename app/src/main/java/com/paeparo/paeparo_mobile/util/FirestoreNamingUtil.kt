package com.paeparo.paeparo_mobile.util

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object FirestoreNamingUtil {
    private val gson: Gson = GsonBuilder()
        .setFieldNamingPolicy(com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    fun <T> toSerializedMap(data: T): Map<String, Any> {
        val jsonString = gson.toJson(data)
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}