package com.paeparo.paeparo_mobile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val activityContext: Context = this
    private val SPLASH_TIME: Long = 2000
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        googleSignInLauncher = FirebaseManager.createGoogleLoginLauncher(this, onSuccess = {
            Toast.makeText(this, "자동 로그인: 사용자 인증에 성공했습니다", Toast.LENGTH_SHORT).show()

            mainScope.launch {
                val userRegisteredResult: Result<FirebaseConstants.RegistrationStatus> =
                    FirebaseManager.checkUserRegistered(activityContext)

                if (userRegisteredResult.isSuccess) { // 사용자 등록 여부 확인을 성공할 경우
                    when (userRegisteredResult.getOrNull()!!) {
                        FirebaseConstants.RegistrationStatus.REGISTERED -> {
                            val intent = Intent(activityContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        FirebaseConstants.RegistrationStatus.NICKNAME_NOT_REGISTERED -> {
                            // TODO: [석민재] 회원가입 Activity에서 닉네임 입력화면 표시
                            Toast.makeText(
                                activityContext, "닉네임을 설정해주세요", Toast.LENGTH_SHORT
                            ).show()
                        }
                        FirebaseConstants.RegistrationStatus.DETAIL_INFO_NOT_REGISTERED -> {
                            // TODO: [석민재] 회원가입 Activity에서 세부정보 입력화면 표시
                            Toast.makeText(
                                activityContext, "세부정보를 설정해주세요", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else { // 사용자 등록 여부 확인을 실패할 경우
                    Toast.makeText(
                        activityContext, "사용자 인증에 실패했습니다. 나중에 다시 시도해주세요", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }, onFailure = {
            Toast.makeText(this, "자동 로그인: 사용자 인증에 실패했습니다", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })

        // Google 로그인 결과를 처리하는 launcher를 LoginActivity에서 생성
        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseManager.getCurrentUser() == null) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                FirebaseManager.loginWithGoogle(activityContext, googleSignInLauncher)
            }
        }, SPLASH_TIME)
    }

    override fun onDestroy() {
        mainScope.cancel()
        super.onDestroy()
    }
}