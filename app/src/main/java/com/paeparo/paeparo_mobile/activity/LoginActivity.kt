package com.paeparo.paeparo_mobile.activity

import android.content.Context
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
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val activityContext: Context = this
    private lateinit var ivPaeParoLogo: ImageView
    private lateinit var tvPaeParo: TextView
    private lateinit var tvLoginTitle: TextView
    private lateinit var btnGoogleLogin: ConstraintLayout
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_login)

        ivPaeParoLogo = findViewById(R.id.iv_paeparo_logo)
        tvPaeParo = findViewById(R.id.tv_paeparo)
        tvLoginTitle = findViewById(R.id.tv_login_title)
        btnGoogleLogin = findViewById(R.id.btn_google_login)

        startAnimation()

        // Google 로그인 결과를 처리하는 launcher를 LoginActivity에서 생성
        googleSignInLauncher = FirebaseManager.createGoogleLoginLauncher(this, onSuccess = {
            Toast.makeText(this, "사용자 인증에 성공했습니다", Toast.LENGTH_SHORT).show()

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
            Toast.makeText(this, "사용자 인증에 실패했습니다. 나중에 다시 시도해주세요", Toast.LENGTH_SHORT).show()
        })

        // Firebase Auth 로그인 테스트 코드
        btnGoogleLogin.setOnClickListener {
            // 구글 로그인 처리
            FirebaseManager.loginWithGoogle(this, googleSignInLauncher)
        }
    }

    private fun startAnimation() {
        tvPaeParo.alpha = 0f
        tvPaeParo.translationY = -dpToPx(16f)

        tvLoginTitle.alpha = 0f
        tvLoginTitle.translationY = dpToPx(16f)

        btnGoogleLogin.alpha = 0f

        tvPaeParo.animate().translationY(0f).alpha(1f).setDuration(2000)
            .setInterpolator(DecelerateInterpolator()).start()
        tvLoginTitle.animate().translationY(0f).alpha(1f).setDuration(2000)
            .setInterpolator(DecelerateInterpolator()).start()
        btnGoogleLogin.animate().alpha(1f).setDuration(2000).setInterpolator(LinearInterpolator())
            .start()
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        )
    }

    override fun onDestroy() {
        mainScope.cancel()
        super.onDestroy()
    }
}