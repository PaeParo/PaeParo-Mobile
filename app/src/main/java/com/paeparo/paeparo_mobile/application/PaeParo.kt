package com.paeparo.paeparo_mobile.application

import android.app.Application

class PaeParo : Application() {
    var userId: String = ""
    var nickname: String = ""

    companion object{
    }

    // Application 객체가 생성될 때 호출
    override fun onCreate() {
        super.onCreate()

        // 초기화 작업이 필요할 경우 아래에 작성
    }
}