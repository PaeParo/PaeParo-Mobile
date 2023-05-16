package com.paeparo.paeparo_mobile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
import com.paeparo.paeparo_mobile.databinding.ActivityNicknameBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.launch

class NickNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // EditText의 TextWatcher 등록
        binding.edtNickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 입력한 글자를 tv_nickname_name_left TextView에 표시
                binding.tvNicknameNameLeft.text = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.root.setOnClickListener {
            hideKeyboard()
        }

        binding.btnNicknameNext.setOnClickListener {
            val nickname: String = binding.edtNickname.text.toString().trim()

            if (TextUtils.isEmpty(nickname)) {
                binding.edtNickname.error = "닉네임을 입력하세요."
                return@setOnClickListener
            } else if (!isNicknameValid(nickname)) {
                binding.edtNickname.error = "닉네임은 특수문자와 공백을 제외한 2~12자 이내로 입력해주세요."
                return@setOnClickListener
            }

            lifecycleScope.launch {
                Log.i("NickNameActivity", "닉네임 변경 요청: " + getPaeParo().userId)
                val result = FirebaseManager.updateUserNickname(getPaeParo().userId, nickname)

                if (result.isSuccess) {
                    getPaeParo().nickname = nickname
                    Toast.makeText(
                        this@NickNameActivity, "반갑습니다 ${nickname}님", Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@NickNameActivity, PersonalActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    when (result.type) {
                        FirebaseConstants.ResponseCodes.NICKNAME_ALREADY_IN_USE -> {
                            Toast.makeText(
                                this@NickNameActivity, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this@NickNameActivity,
                                "닉네임 변경에 실패했습니다. 나중에 다시 시도해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun isNicknameValid(nickname: String): Boolean {
        val regex = "^[a-zA-Z가-힣0-9]*$"
        return nickname.matches(regex.toRegex()) && nickname.length in 2..12
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}