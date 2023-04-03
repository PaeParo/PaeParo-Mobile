package com.paeparo.paeparo_mobile.activity

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ActivityNicknameBinding

class NickNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val btnNicknameNext: Button = findViewById(R.id.btn_nickname_next)
        btnNicknameNext.setOnClickListener {
            val nickname: String = binding.edtNickname.text.toString().trim()

            if (TextUtils.isEmpty(nickname)) {
                binding.edtNickname.error = "닉네임을 입력하세요."
                return@setOnClickListener
            } else if (!isNicknameValid(nickname)) {
                binding.edtNickname.error = "닉네임은 특수문자와 공백을 제외한 2~12자 이내로 입력해주세요."
                return@setOnClickListener
            }
        }


    }

    private fun isNicknameValid(nickname: String): Boolean {
        return true
    }


}