package com.paeparo.paeparo_mobile.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
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

        val builder = AlertDialog.Builder(this) //다이어로그 창
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

        binding.userDetail.setText("회원탈퇴를 합니다.")

        binding.userLayout.setOnClickListener {
            //startActivity(Intent(this, MyHomeSettingsUserActivity::class.java))
            builder.setTitle("회원탈퇴를 하시겠습니까?")
                .setMessage("일정 및 댓글 모든 정보가 삭제될것입니다.")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
                        //DB 삭제
                        startActivity(Intent(this, LoginActivity::class.java))
                        ActivityCompat.finishAffinity(this)
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, id ->
                        //빈칸
                    })
            builder.show()
        }

        binding.logout.setOnClickListener(){
            FirebaseManager.logoutWithGoogle(this)
            startActivity(Intent(this, LoginActivity::class.java))
            ActivityCompat.finishAffinity(this)
        }
        binding.back.setOnClickListener {
            finish()
        }
        binding.settings.setText("설정")
    }

    //ShardPreferencesKey 로드
    private fun loadData(){
        binding.alram.isChecked=SharedPreferencesManager.get(SharedPreferencesKey.KEY_SETTINGS_ALARM,true) ?: true
    }
}