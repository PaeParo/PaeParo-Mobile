package com.paeparo.paeparo_mobile.constant

object FirebaseConstants {
    sealed class CheckRegistrationResult {
        object Registered : CheckRegistrationResult()
        object NicknameNotSet : CheckRegistrationResult()
        object DetailInfoNotSet : CheckRegistrationResult()
        data class OtherError(val exception: Throwable) : CheckRegistrationResult()
    }

    sealed class UpdateNicknameResult {
        object UpdateSuccess : UpdateNicknameResult()
        object DuplicateError : UpdateNicknameResult()
        data class OtherError(val exception: Throwable) : UpdateNicknameResult()
    }
}