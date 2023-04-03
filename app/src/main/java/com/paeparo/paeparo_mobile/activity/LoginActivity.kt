package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private lateinit var ivLoginPaeParoLogo: ImageView
    private lateinit var tvLoginPaeParo: TextView
    private lateinit var tvLoginTitle: TextView
    private lateinit var btnLoginGoogleLogin: ConstraintLayout
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_login)

        ivLoginPaeParoLogo = findViewById(R.id.iv_login_paeparo_logo)
        tvLoginPaeParo = findViewById(R.id.tv_login_paeparo)
        tvLoginTitle = findViewById(R.id.tv_login_title)
        btnLoginGoogleLogin = findViewById(R.id.btn_login_google_login)

        startAnimation()

        // Google 로그인 결과를 처리하는 launcher를 LoginActivity에서 생성
        googleSignInLauncher =
            FirebaseManager.createGoogleLoginLauncher(this@LoginActivity, onSuccess = {
                lifecycleScope.launch(Dispatchers.IO) {
                    when (FirebaseManager.checkCurrentUserRegistration(this@LoginActivity)) {
                        is FirebaseConstants.CheckRegistrationResult.Registered -> {
                            val getUserResult =
                                FirebaseManager.getCurrentUserData(this@LoginActivity)

                            withContext(Dispatchers.Main) {
                                if (getUserResult.isSuccess) {
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "사용자 정보를 가져오는데 실패했습니다",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        is FirebaseConstants.CheckRegistrationResult.NicknameNotSet -> {
                            withContext(Dispatchers.Main) {
                                val intent =
                                    Intent(this@LoginActivity, NickNameActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        is FirebaseConstants.CheckRegistrationResult.DetailInfoNotSet -> {
                            withContext(Dispatchers.Main) {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        is FirebaseConstants.CheckRegistrationResult.OtherError -> {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "사용자 등록 여부 확인에 실패했습니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }, onFailure = {
                Toast.makeText(this@LoginActivity, "자동 로그인: 사용자 인증에 실패했습니다", Toast.LENGTH_SHORT)
                    .show()
            })

        // Firebase Auth 로그인 테스트 코드
        btnLoginGoogleLogin.setOnClickListener {
            // 구글 로그인 처리
            FirebaseManager.loginWithGoogle(this@LoginActivity, googleSignInLauncher)
        }
    }

    private fun startAnimation() {
        tvLoginPaeParo.alpha = 0f
        tvLoginPaeParo.translationY = -dpToPx(16f)

        tvLoginTitle.alpha = 0f
        tvLoginTitle.translationY = dpToPx(16f)

        btnLoginGoogleLogin.alpha = 0f

        tvLoginPaeParo.animate().translationY(0f).alpha(1f).setDuration(2000)
            .setInterpolator(DecelerateInterpolator()).start()
        tvLoginTitle.animate().translationY(0f).alpha(1f).setDuration(2000)
            .setInterpolator(DecelerateInterpolator()).start()
        btnLoginGoogleLogin.animate().alpha(1f).setDuration(2000)
            .setInterpolator(LinearInterpolator())
            .start()
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}