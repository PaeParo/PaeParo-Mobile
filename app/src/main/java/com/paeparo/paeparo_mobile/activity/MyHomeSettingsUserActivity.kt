package com.paeparo.paeparo_mobile.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeSettingsUserBinding

class MyHomeSettingsUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeSettingsUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeSettingsUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 상단 액션바

        val builder = AlertDialog.Builder(this) //다이어로그 창

        binding.userDelete.setOnClickListener {
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
    }

    //상단 바 제어
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // 뒤로가기 동작 수행
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}