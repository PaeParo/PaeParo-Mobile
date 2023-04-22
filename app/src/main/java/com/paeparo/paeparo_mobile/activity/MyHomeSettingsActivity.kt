package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        if(!(binding.switch1.isChecked)){
            SharedPreferencesManager.set(SharedPreferencesKey.KEY_SETTINGS_TEST,true)
        }else{
            SharedPreferencesManager.set(SharedPreferencesKey.KEY_SETTINGS_TEST,false)
        }
        binding.logoutButton.setOnClickListener(){
            FirebaseManager.logoutWithGoogle(this)
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    //ShardPreferencesKey 로드
    private fun loadData(){
        binding.switch1.isChecked=SharedPreferencesManager.get(SharedPreferencesKey.KEY_SETTINGS_TEST,false) ?: false
    }
}