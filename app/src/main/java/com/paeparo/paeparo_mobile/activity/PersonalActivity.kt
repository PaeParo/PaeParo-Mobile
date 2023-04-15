package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.ActivityPersonalBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.launch

//수정중(여러분->닉네임, 버튼그룹(연령대), 취향3순위(3개버튼만 선택, 3개 배열로 넘기기))

class PersonalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextButton.setOnClickListener {

            lifecycleScope.launch {
                val gender = when (binding.rgPersonalGender.checkedRadioButtonId) {
                    R.id.btn_personal_gender_male -> "male"
                    R.id.btn_personal_gender_female -> "female"
                    else -> null
                }
                val age = when (binding.rgPersonalAge1.checkedRadioButtonId) {
                    R.id.btn_personal_age_teens -> 10
                    R.id.btn_personal_age_twenty -> 20
                    R.id.btn_personal_age_thirty -> 30
//                    R.id.btn_personal_age_forty -> 40
//                    R.id.btn_personal_age_fifty -> 50
                    else -> null
                }
                if (gender != null && age != null) {
                    val updateFields = mapOf(
                        "gender" to gender,
                        "age" to age
                    )
                    val result =
                        FirebaseManager.updateUserDetailInfo(getPaeParo().userId, updateFields)
                    if (result.isSuccess) {
                        Toast.makeText(
                            this@PersonalActivity, "성별과 연령 정보가 업데이트되었습니다", Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@PersonalActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@PersonalActivity, "성별과 연령 정보 업데이트에 실패했습니다", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (gender == null) {
                    Toast.makeText(this@PersonalActivity, "성별을 선택해주세요", Toast.LENGTH_SHORT).show()
                } else if (age == null) {
                    Toast.makeText(this@PersonalActivity, "연령대를 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
