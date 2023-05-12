package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.paeparo.paeparo_mobile.constant.SharedPreferencesKey
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeSettingsBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.manager.SharedPreferencesManager



class MyHomeSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadData()

        //초기 세팅 추후에 코드 바꿀 예정
        if(binding.alram.isChecked==true){
            binding.alramDetail.setText("알람을 받습니다.")
        }else{
            binding.alramDetail.setText("알람을 받지 않습니다.")
        }

        //알람 스위치
        binding.alram.setOnCheckedChangeListener {_,isChecked ->
            if(isChecked){
                binding.alramDetail.setText("알람을 받습니다.")
                SharedPreferencesManager.set(SharedPreferencesKey.KEY_SETTINGS_ALARM,true)
            }else{
                binding.alramDetail.setText("알람을 받지 않습니다.")
                SharedPreferencesManager.set(SharedPreferencesKey.KEY_SETTINGS_ALARM,false)
            }
        }

        binding.logoutButton.setOnClickListener(){
            FirebaseManager.logoutWithGoogle(this)
            startActivity(Intent(this, LoginActivity::class.java))
            ActivityCompat.finishAffinity(this)
        }
    }

    //ShardPreferencesKey 로드
    private fun loadData(){
        binding.alram.isChecked=SharedPreferencesManager.get(SharedPreferencesKey.KEY_SETTINGS_ALARM,true) ?: true

    }
}