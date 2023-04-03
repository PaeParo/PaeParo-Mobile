package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.R
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
            FirebaseManager.createGoogleLoginLauncher(this@SplashActivity, onSuccess = {
                lifecycleScope.launch(Dispatchers.IO) {
                    when (FirebaseManager.checkCurrentUserRegistration(this@SplashActivity)) {
                        is FirebaseConstants.CheckRegistrationResult.Registered -> {
                            val getUserResult =
                                FirebaseManager.getCurrentUserData(this@SplashActivity)

                            withContext(Dispatchers.Main) {
                                if (getUserResult.isSuccess) {
                                    val intent =
                                        Intent(this@SplashActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@SplashActivity,
                                        "사용자 정보를 가져오는데 실패했습니다",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        is FirebaseConstants.CheckRegistrationResult.NicknameNotSet -> {
                            withContext(Dispatchers.Main) {
                                val intent =
                                    Intent(this@SplashActivity, NickNameActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        is FirebaseConstants.CheckRegistrationResult.DetailInfoNotSet -> {
                            withContext(Dispatchers.Main) {
                                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        is FirebaseConstants.CheckRegistrationResult.OtherError -> {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@SplashActivity,
                                    "사용자 등록 여부 확인에 실패했습니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }, onFailure = {
                Toast.makeText(this@SplashActivity, "자동 로그인: 사용자 인증에 실패했습니다", Toast.LENGTH_SHORT)
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