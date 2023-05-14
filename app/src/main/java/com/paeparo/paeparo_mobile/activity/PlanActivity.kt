package com.paeparo.paeparo_mobile.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.tabs.TabLayoutMediator
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.adapter.PlanAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityPlanBinding
import com.paeparo.paeparo_mobile.model.Trip
import kotlin.math.roundToInt
/*
    일정 세부정보를 일자별로(Day1,Day2) 보는 Activiy
 */
class PlanActivity : AppCompatActivity() {
    private val binding: ActivityPlanBinding by lazy {
        ActivityPlanBinding.inflate(layoutInflater) //viewbinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vpPlan.adapter = PlanAdapter(this)
        val trip = getTrip()



    }

    // deprecated되어서 sdk 버전에 따라 처리
    fun getTrip() : Trip{
        val model : Trip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("data", Trip::class.java)!!
        } else {
            intent.getSerializableExtra("data") as Trip
        }
        return model
    }
}