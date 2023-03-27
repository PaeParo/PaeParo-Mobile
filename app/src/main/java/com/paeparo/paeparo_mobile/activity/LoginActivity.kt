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
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.manager.FirebaseManager

class LoginActivity : AppCompatActivity() {
    private lateinit var ivPaeParoLogo: ImageView
    private lateinit var tvPaeParo: TextView
    private lateinit var tvLoginTitle: TextView
    private lateinit var btnGoogleLogin: ConstraintLayout
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

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
        googleSignInLauncher = FirebaseManager.createGoogleLoginLauncher(this,
            onSuccess = {
                Toast.makeText(this, "Firebase auth success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            },
            onFailure = {
                Toast.makeText(this, "Firebase auth failed", Toast.LENGTH_SHORT).show()
            })

        // Firebase Auth 로그인 테스트 코드
        btnGoogleLogin.setOnClickListener {
            // 구글 로그인 처리
            FirebaseManager.loginWithGoogle(this, googleSignInLauncher)
        }
    }

    private fun startAnimation(){
        tvPaeParo.alpha = 0f
        tvPaeParo.translationY = -dpToPx(16f)

        tvLoginTitle.alpha = 0f
        tvLoginTitle.translationY = dpToPx(16f)

        btnGoogleLogin.alpha = 0f

        tvPaeParo.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(2000)
            .setInterpolator(DecelerateInterpolator())
            .start()
        tvLoginTitle.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(2000)
            .setInterpolator(DecelerateInterpolator())
            .start()
        btnGoogleLogin.animate()
            .alpha(1f)
            .setDuration(2000)
            .setInterpolator(LinearInterpolator())
            .start()
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }
}