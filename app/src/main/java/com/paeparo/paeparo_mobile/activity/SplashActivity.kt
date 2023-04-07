package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME: Long = 2000
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        googleSignInLauncher =
            FirebaseManager.createGoogleLoginLauncher(
                this@SplashActivity,
                onGoogleSignInSuccess = { idToken ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val loginResult: Result<String> = FirebaseManager.login(idToken)

                        if (loginResult.isSuccess) {
                            when (loginResult.getOrNull()) {
                                FirebaseConstants.ResponseCodes.SUCCESS -> {
                                    val getUserResult =
                                        FirebaseManager.getUser(FirebaseManager.getCurrentUser()!!.uid)
                                    withContext(Dispatchers.Main) {
                                        if (getUserResult.isSuccess) {
                                            getPaeParo().userId = getUserResult.getOrNull()!!.userId
                                            getPaeParo().nickname =
                                                getUserResult.getOrNull()!!.nickname

                                            val intent = Intent(
                                                this@SplashActivity,
                                                MainActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@SplashActivity,
                                                "자동 로그인: 사용자 인증에 실패했습니다",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            val intent = Intent(
                                                this@SplashActivity,
                                                LoginActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                }
                                FirebaseConstants.ResponseCodes.NICKNAME_NOT_SET -> {
                                    withContext(Dispatchers.Main) {
                                        val intent = Intent(
                                            this@SplashActivity,
                                            NickNameActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                                FirebaseConstants.ResponseCodes.DETAIL_INFO_NOT_SET -> {
                                    withContext(Dispatchers.Main) {
                                        val intent =
                                            Intent(this@SplashActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@SplashActivity,
                                    "자동 로그인: 사용자 인증에 실패했습니다",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                },
                onGoogleSignInFailed = {
                    Toast.makeText(
                        this@SplashActivity,
                        "자동 로그인: 사용자 인증에 실패했습니다",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                })

        // Google 로그인 결과를 처리하는 launcher를 LoginActivity에서 생성
        lifecycleScope.launch {
            delay(SPLASH_TIME)

            if (FirebaseManager.getCurrentUser() == null) {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                FirebaseManager.loginWithGoogle(this@SplashActivity, googleSignInLauncher)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}