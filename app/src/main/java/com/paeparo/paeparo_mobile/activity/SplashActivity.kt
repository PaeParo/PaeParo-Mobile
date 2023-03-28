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
import com.paeparo.paeparo_mobile.manager.FirebaseManager

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME: Long = 2000
    private val context: Context = this
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        googleSignInLauncher = FirebaseManager.createGoogleLoginLauncher(context,
            onSuccess = {
                Toast.makeText(this, "Auto Login: Firebase auth success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            },
            onFailure = {
                Toast.makeText(this, "Auto Login: Firebase auth failed", Toast.LENGTH_SHORT).show()
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
                FirebaseManager.loginWithGoogle(context, googleSignInLauncher)
            }
        }, SPLASH_TIME)
    }
}