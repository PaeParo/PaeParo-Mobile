package com.paeparo.paeparo_mobile.application

import android.app.Application
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.manager.SharedPreferencesManager

class PaeParo : Application() {
    var userId: String = ""
    var nickname: String = ""

    companion object{
        var sharedPreferencesManager: SharedPreferencesManager? = null
        var firebaseManager: FirebaseManager? = null
    }

    // Application 객체가 생성될 때 호출
    override fun onCreate() {
        super.onCreate()

        // 초기화 작업이 필요할 경우 아래에 작성
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this)
        firebaseManager = FirebaseManager.instance
    }
}