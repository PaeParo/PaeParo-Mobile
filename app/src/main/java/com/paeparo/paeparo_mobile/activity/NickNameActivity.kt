package com.paeparo.paeparo_mobile.activity

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paeparo.paeparo_mobile.databinding.ActivityNicknameBinding


class NickNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicknameBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    // private val networkScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnNicknameNext.setOnClickListener {
            val nickname: String = binding.edtNickname.text.toString().trim()

            if (TextUtils.isEmpty(nickname)) {
                binding.edtNickname.error = "닉네임을 입력하세요."
                return@setOnClickListener
            } else if (!isNicknameValid(nickname)) {
                binding.edtNickname.error = "닉네임은 특수문자와 공백을 제외한 2~12자 이내로 입력해주세요."
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user != null) {
                val nicknameMap = hashMapOf("nickname" to nickname)
                firestore.collection("users").document(user.uid)
                    .update(nicknameMap as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@NickNameActivity, "닉네임이 설정되었습니다.", Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@NickNameActivity, "닉네임 설정에 실패했습니다.", Toast.LENGTH_SHORT
                        ).show()
                    }
            }

        }
    }
//    if(isNicknameValid(nick))
            //TODO: 닉네임 중복 검사 처리
            //닉네임 중복검사
//            networkScope.launch {
//                val result: Result<String> = FirebaseManager.getUserIdByNickname(nick)
//                result.onSuccess {
                    // TODO: 닉네임 중복인 경우
//                }
//                result.onFailure {
                    // TODO: 닉네임 중복이 없는거
                    //FirebaseManager.updateCurrentUserNickname()
//                }



    private fun isNicknameValid(nickname: String): Boolean {
        val regex = "^[a-zA-Z가-힣0-9]*$"
        return nickname.matches(regex.toRegex()) && nickname.length in 2..10
    }

}