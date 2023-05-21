package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.adapter.MyHomeFaqAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeFaqBinding

class MyHomeFaqActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeFaqBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.apply {
            // 뒤로가기 버튼 활성화
            setDisplayHomeAsUpEnabled(true)
            // 액션바 타이틀 설정
            title = "FAQ"
        }

        binding.faqRecyclerView.apply{
            adapter=MyHomeFaqAdapter()
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }


    //상단 바 제어
    override fun onSupportNavigateUp(): Boolean {
        // 뒤로가기 버튼 클릭 시 액티비티 종료
        onBackPressed()
        return true
    }
}