package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.adapter.PlanGenerateAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityPlanGenerateBinding
import timber.log.Timber

class PlanGenerateActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlanGenerateBinding
    private lateinit var viewPager : ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanGenerateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()

    }

    private fun bind() {
        with(binding) {
            viewPager = vpPlanGenerate
            viewPager.adapter = PlanGenerateAdapter(this@PlanGenerateActivity)
            viewPager.isUserInputEnabled = false // 슬라이드 금지를 통한 페이지 전환 방지.
        }
    }

    override fun onBackPressed() {
        if(viewPager.currentItem==0) finish()
        viewPager.currentItem--
    }
}




