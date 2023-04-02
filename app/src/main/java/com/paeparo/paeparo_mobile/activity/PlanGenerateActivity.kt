package com.paeparo.paeparo_mobile.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.paeparo.paeparo_mobile.PlanGenerateAdapter
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ActivityPlanGenerateBinding
import java.text.SimpleDateFormat
import java.util.*

class PlanGenerateActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPlanGenerateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanGenerateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.vpPlanGenerate

        viewPager.apply {
            adapter = PlanGenerateAdapter(context as FragmentActivity)
        }
    }
}


