package com.paeparo.paeparo_mobile.constant

object FirebaseConstants {
    enum class RegistrationStatus {
        NICKNAME_NOT_REGISTERED,
        DETAIL_INFO_NOT_REGISTERED,
        REGISTERED
    }

    sealed class UpdateNicknameResult {
        object UpdateSuccess : UpdateNicknameResult()
        object DuplicateError : UpdateNicknameResult()
        data class OtherError(val exception: Throwable) : UpdateNicknameResult()
    }
}