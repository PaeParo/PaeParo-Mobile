package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.constant.FirebaseConstants
import com.paeparo.paeparo_mobile.databinding.ActivityNicknameBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NickNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val networkScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // EditText의 TextWatcher 등록
        binding.edtNickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 입력한 글자를 tv_nickname_name_left TextView에 표시
                binding.tvNicknameNameLeft.text = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.btnNicknameNext.setOnClickListener {
            val nickname: String = binding.edtNickname.text.toString().trim()

            if (TextUtils.isEmpty(nickname)) {
                binding.edtNickname.error = "닉네임을 입력하세요."
                return@setOnClickListener
            } else if (!isNicknameValid(nickname)) {
                binding.edtNickname.error = "닉네임은 특수문자와 공백을 제외한 2~12자 이내로 입력해주세요."
                return@setOnClickListener
            }

            networkScope.launch {
                val result =
                    FirebaseManager.updateUserNickname(getPaeParo().userId, nickname)

                withContext(Dispatchers.Main) {
                    when (result) {
                        is FirebaseConstants.UpdateNicknameResult.UpdateSuccess -> {
                            this@NickNameActivity.getPaeParo().nickname = result.nickname
                            Toast.makeText(
                                this@NickNameActivity,
                                "닉네임 등록에 성공했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@NickNameActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        is FirebaseConstants.UpdateNicknameResult.DuplicateError -> {
                            Toast.makeText(
                                this@NickNameActivity,
                                "이미 존재하는 닉네임입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is FirebaseConstants.UpdateNicknameResult.OtherError -> {
                            Toast.makeText(
                                this@NickNameActivity,
                                "닉네임 등록에 실패했습니다.",
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

}