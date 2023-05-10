package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
import com.paeparo.paeparo_mobile.databinding.ActivityLoginBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimation()

        binding.ciLoginLoading.visibility = android.view.View.GONE

        // Google 로그인 결과를 처리하는 launcher를 LoginActivity에서 생성
        googleSignInLauncher =
            FirebaseManager.createGoogleLoginLauncher(
                this@LoginActivity,
                onSuccess = {
                    binding.ciLoginLoading.visibility = android.view.View.GONE
                    when (it.type) {
                        FirebaseConstants.ResponseCodes.ALL_DATA_SET -> {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        FirebaseConstants.ResponseCodes.NICKNAME_NOT_SET -> {
                            val intent = Intent(this@LoginActivity, NickNameActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        FirebaseConstants.ResponseCodes.DETAIL_INFO_NOT_SET -> {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                },
                onFailure = {
                    binding.ciLoginLoading.visibility = android.view.View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 실패했습니다. 나중에 다시 시도해주세요.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                })

        // Firebase Auth 로그인 테스트 코드
        binding.btnLoginGoogleLogin.setOnClickListener {
            // 구글 로그인 처리
            binding.ciLoginLoading.visibility = android.view.View.VISIBLE
            FirebaseManager.launchGoogleLoginTask(this@LoginActivity, googleSignInLauncher)
        }
    }

    private fun startAnimation() {
        binding.tvLoginPaeparo.alpha = 0f
        binding.tvLoginPaeparo.translationY = -dpToPx(16f)

        binding.tvLoginTitle.alpha = 0f
        binding.tvLoginTitle.translationY = dpToPx(16f)

        binding.btnLoginGoogleLogin.alpha = 0f

        binding.tvLoginPaeparo.animate().translationY(0f).alpha(1f).setDuration(2000)
            .setInterpolator(DecelerateInterpolator()).start()
        binding.tvLoginTitle.animate().translationY(0f).alpha(1f).setDuration(2000)
            .setInterpolator(DecelerateInterpolator()).start()
        binding.btnLoginGoogleLogin.animate().alpha(1f).setDuration(2000)
            .setInterpolator(LinearInterpolator())
            .start()
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        )
    }
}