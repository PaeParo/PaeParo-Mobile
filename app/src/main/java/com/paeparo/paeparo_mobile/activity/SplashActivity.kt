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
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME: Long = 2000
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val mainScope = lifecycleScope
    private val networkScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        googleSignInLauncher =
            FirebaseManager.createGoogleLoginLauncher(this@SplashActivity, onSuccess = {
                Toast.makeText(this@SplashActivity, "자동 로그인: 사용자 인증에 성공했습니다", Toast.LENGTH_SHORT)
                    .show()

                networkScope.launch {
                    val userRegisteredResult: Result<FirebaseConstants.RegistrationStatus> =
                        FirebaseManager.checkUserRegistered(this@SplashActivity)

                    if (userRegisteredResult.isSuccess) { // 사용자 등록 여부 확인을 성공할 경우
                        when (userRegisteredResult.getOrNull()!!) {
                            FirebaseConstants.RegistrationStatus.REGISTERED -> {
                                val getUserResult = FirebaseManager.getCurrentUserData(this@SplashActivity)

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
                            FirebaseConstants.RegistrationStatus.NICKNAME_NOT_REGISTERED -> {
                                val intent =
                                    Intent(this@SplashActivity, NickNameActivity::class.java)
                                startActivity(intent)
                                finish()

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@SplashActivity, "닉네임을 설정해주세요", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            FirebaseConstants.RegistrationStatus.DETAIL_INFO_NOT_REGISTERED -> {
                                // TODO: [석민재] 회원가입 Activity에서 세부정보 입력화면 표시
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@SplashActivity, "세부정보를 설정해주세요", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else { // 사용자 등록 여부 확인을 실패할 경우
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SplashActivity,
                                "사용자 인증에 실패했습니다. 나중에 다시 시도해주세요",
                                Toast.LENGTH_SHORT
                            ).show()
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
        mainScope.launch {
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
        networkScope.cancel()
        super.onDestroy()
    }
}