package com.paeparo.paeparo_mobile.constant

object FirebaseConstants {
    object ResponseCodes {
        const val INVALID_ID_TOKEN = "auth/invalid-id-token"

        const val SUCCESS = "success"
        const val FAILURE = "failure"
        const val SERVER_ERROR = "server-error"
        const val CLIENT_ERROR = "client-error"

        const val USER_NOT_FOUND = "user/user-not-found"
        const val ALL_DATA_SET = "user/all-data-set"
        const val NICKNAME_NOT_SET = "user/nickname-not-set"
        const val NICKNAME_ALREADY_IN_USE = "user/nickname-already-in-use"
        const val DETAIL_INFO_NOT_SET = "user/detail-info-not-set"

        const val TRIP_NOT_FOUND = "trip/trip-not-found"

        const val EVENT_NOT_FOUND = "event/event-not-found"

        const val POST_NOT_FOUND = "post/post-not-found"

        const val COMMENT_NOT_FOUND = "comment/comment-not-found"
    }

    sealed class CheckRegistrationResult {
        object Registered : CheckRegistrationResult()
        object NicknameNotSet : CheckRegistrationResult()
        object DetailInfoNotSet : CheckRegistrationResult()
        data class OtherError(val exception: Throwable) : CheckRegistrationResult()
    }

    sealed class UpdateNicknameResult {
        data class UpdateSuccess(val nickname: String) : UpdateNicknameResult()
        object DuplicateError : UpdateNicknameResult()
        data class OtherError(val exception: Throwable) : UpdateNicknameResult()
    }
}