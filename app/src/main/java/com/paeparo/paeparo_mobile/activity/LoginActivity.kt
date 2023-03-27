package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.manager.FirebaseManager

class LoginActivity : AppCompatActivity() {
    private lateinit var btnGoogleLogin: ConstraintLayout
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        btnGoogleLogin = findViewById(R.id.btn_google_login)
        btnGoogleLogin.setOnClickListener {
            // 구글 로그인 처리
            FirebaseManager.loginWithGoogle(this, googleSignInLauncher)
        }
    }
}