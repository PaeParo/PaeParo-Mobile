package com.paeparo.paeparo_mobile.model

import com.paeparo.paeparo_mobile.constant.FirebaseConstants

data class FirebaseResult<T>(
    val isSuccess: Boolean,
    val type: String? = null,
    val data: T? = null,
    val error: Throwable? = null
) {
    companion object {
        fun <T> success(type: String? = null, data: T? = null): FirebaseResult<T> {
            return FirebaseResult(true, type, data)
        }

        fun <T> make(result: Map<*, *>): FirebaseResult<T> {
            return FirebaseResult(
                result["result"] as String == FirebaseConstants.ResponseCodes.SUCCESS,
                result["type"] as String?,
                null
            )
        }

        fun <T> failure(type: String? = null, error: Throwable? = null): FirebaseResult<T> {
            return FirebaseResult(false, type, null, error)
        }
    }
}
