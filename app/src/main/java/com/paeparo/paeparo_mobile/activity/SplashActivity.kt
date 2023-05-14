package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
import com.paeparo.paeparo_mobile.databinding.ActivitySplashBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_TIME: Long = 2000
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ciSplashLoading.visibility = View.INVISIBLE

        googleSignInLauncher =
            FirebaseManager.createGoogleLoginLauncher(
                this@SplashActivity,
                onSuccess = {
                    binding.ciSplashLoading.visibility = View.INVISIBLE
                    when (it.type) {
                        FirebaseConstants.ResponseCodes.ALL_DATA_SET -> {
                            val intent = Intent(this@SplashActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        FirebaseConstants.ResponseCodes.NICKNAME_NOT_SET -> {
                            val intent = Intent(this@SplashActivity, NickNameActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        FirebaseConstants.ResponseCodes.DETAIL_INFO_NOT_SET -> {
                            val intent = Intent(this@SplashActivity, PersonalActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                },
                onFailure = {
                    binding.ciSplashLoading.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@SplashActivity,
                        "자동 로그인 실패했습니다. 다시 로그인해주세요.",
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
                binding.ciSplashLoading.visibility = View.VISIBLE
                FirebaseManager.launchGoogleLoginTask(this@SplashActivity, googleSignInLauncher)
            }
        }
    }
}