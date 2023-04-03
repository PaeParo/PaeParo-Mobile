package com.paeparo.paeparo_mobile.activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ActivityNicknameBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NickNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameBinding
    private val networkScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNicknameNext.setOnClickListener {
            val nick = binding.edtNickname.text.toString()

            // 버튼을 눌렀을 떄,
            if(isNicknameValid(nick))
            //TODO: 닉네임 중복 검사 처리
            //닉네임 중복검사
            networkScope.launch {
                val result: Result<String> = FirebaseManager.getUserIdByNickname(nick)
                result.onSuccess {
                    // TODO: 닉네임 중복인 경우
                }
                result.onFailure {
                    // TODO: 닉네임 중복이 없는거
                    //FirebaseManager.updateCurrentUserNickname()
                }
            }

            val nickname: String = binding.edtNickname.text.toString()

            if (TextUtils.isEmpty(nickname)) {
                binding.edtNickname.error = "닉네임을 입력하세요."
                return@setOnClickListener
            } else if (!isNicknameValid(nickname)) {
                binding.edtNickname.error = "닉네임은 특수문자와 공백을 제외한 2~12자 이내로 입력해주세요."
                return@setOnClickListener
            }
        }


    }

    /**
     * Is nickname valid
     *
     * @param nickname 사용자 닉네임
     * @return 성공 시 반환
     */
    private fun isNicknameValid(nickname: String): Boolean {
        return true
    }


}