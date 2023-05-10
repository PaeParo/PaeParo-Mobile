package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.ActivityPersonalBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.launch

class PersonalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalBinding
    private val ageCheckBoxes: List<CheckBox> by lazy {
        listOf(
            binding.cbPersonalAgeTeens,
            binding.cbPersonalAgeTwenty,
            binding.cbPersonalAgeThirty,
            binding.cbPersonalAgeForty,
            binding.cbPersonalAgeFifty
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //personal title에 닉네임 추가
        val nickname = getPaeParo().nickname
        binding.tvPersonalTitle3.text =
            String.format(getString(R.string.sample_text_tv_personal_title3), nickname)

        ageCheckBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    ageCheckBoxes.filterNot { it == checkBox }.forEach { it.isChecked = false }
                }
            }
        }

        val styleCheckboxList = listOf(
            findViewById<CheckBox>(R.id.cb_personal_style_style1),
            findViewById<CheckBox>(R.id.cb_personal_style_style2),
            findViewById<CheckBox>(R.id.cb_personal_style_style3),
            findViewById<CheckBox>(R.id.cb_personal_style_style4),
            findViewById<CheckBox>(R.id.cb_personal_style_style5),
            findViewById<CheckBox>(R.id.cb_personal_style_style6),
            findViewById<CheckBox>(R.id.cb_personal_style_style7)
        )

        val maxCheckboxCount = 3
        val checkedStyleCheckboxList = mutableListOf<CheckBox>()

        for (styleCheckbox in styleCheckboxList) {
            styleCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (checkedStyleCheckboxList.size >= maxCheckboxCount) {
                        val lastCheckedCheckbox = checkedStyleCheckboxList.getOrNull(2)
                        lastCheckedCheckbox?.isChecked = false
                        checkedStyleCheckboxList.remove(lastCheckedCheckbox)
                    }
                    checkedStyleCheckboxList.add(styleCheckbox)
                } else {
                    checkedStyleCheckboxList.remove(styleCheckbox)
                }
            }
        }

        binding.nextButton.setOnClickListener {

            lifecycleScope.launch {
                val gender = when (binding.rgPersonalGender.checkedRadioButtonId) {
                    R.id.btn_personal_gender_male -> "male"
                    R.id.btn_personal_gender_female -> "female"
                    else -> null
                }

                val selectedAge = ageCheckBoxes.find { it.isChecked }

                if (gender != null && selectedAge != null && checkedStyleCheckboxList.size == maxCheckboxCount) {
                    val age = getAgeFromCheckBoxId(selectedAge.id)
                    val selectedStyles =
                        checkedStyleCheckboxList.map { getStyleFromCheckBoxId(it.id) }
                    val updateFields = mapOf(
                        "gender" to gender, "age" to age,
                        "travel_style" to selectedStyles
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
                    Toast.makeText(this@PersonalActivity, "성별을 선택해 주세요", Toast.LENGTH_SHORT).show()
                } else if (selectedAge == null) {
                    Toast.makeText(this@PersonalActivity, "연령대를 선택해 주세요", Toast.LENGTH_SHORT).show()
                } else if (checkedStyleCheckboxList.size < maxCheckboxCount) {
                    Toast.makeText(this@PersonalActivity, "취향 세 가지를 선택해 주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun getStyleFromCheckBoxId(checkBoxId: Int): String? {
        return when (checkBoxId) {
            R.id.cb_personal_style_style1 -> getString(R.string.style_style1)
            R.id.cb_personal_style_style2 -> getString(R.string.style_style2)
            R.id.cb_personal_style_style3 -> getString(R.string.style_style3)
            R.id.cb_personal_style_style4 -> getString(R.string.style_style4)
            R.id.cb_personal_style_style5 -> getString(R.string.style_style5)
            R.id.cb_personal_style_style6 -> getString(R.string.style_style6)
            R.id.cb_personal_style_style7 -> getString(R.string.style_style7)
            else -> null
        }
    }

    private fun getAgeFromCheckBoxId(checkBoxId: Int): Int? {
        return when (checkBoxId) {
            R.id.cb_personal_age_teens -> 10
            R.id.cb_personal_age_twenty -> 20
            R.id.cb_personal_age_thirty -> 30
            R.id.cb_personal_age_forty -> 40
            R.id.cb_personal_age_fifty -> 50
            else -> null
        }
    }
}
