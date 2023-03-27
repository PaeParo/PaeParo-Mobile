package com.paeparo.paeparo_mobile.application

import android.app.Application
import com.google.firebase.FirebaseApp
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.manager.SharedPreferencesManager

/**
 * PaeParo class
 *
 * Application 전역에서 사용하는 변수 관리 및 초기화를 담당하는 클래스
 */
class PaeParo : Application() {

    /**
     * 로그인한 사용자 UID
     */
    var userId: String = ""

    /**
     * 로그인한 사용자 닉네임
     */
    var nickname: String = ""

    /**
     * Application 생성 시 실행되며 FirebaseApp 및 Manager 클래스 초기화
     */
    override fun onCreate() {
        super.onCreate()

        // FirebaseApp 초기화
        FirebaseApp.initializeApp(this)

        // Manager 초기화
        SharedPreferencesManager.initializeManager(this)
        FirebaseManager.initializeManager(this)
    }
}