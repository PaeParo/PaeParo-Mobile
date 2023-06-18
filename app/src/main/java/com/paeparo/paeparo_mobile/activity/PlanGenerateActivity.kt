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
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.ActivityPlanGenerateBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class PlanGenerateActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlanGenerateBinding
    private lateinit var viewPager : ViewPager2
    lateinit var trip : Trip
    var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanGenerateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
        trip = Trip()
    }

    private fun bind() {
        with(binding) {
            viewPager = vpPlanGenerate
            viewPager.adapter = PlanGenerateAdapter(this@PlanGenerateActivity)
            viewPager.isUserInputEnabled = false // 슬라이드 금지를 통한 페이지 전환 방지.
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if(viewPager.currentItem==0) finish()
        viewPager.currentItem--
    }

    fun createTrip(){
        if(job != null) return
        val currentUserId = applicationContext.getPaeParo().userId
        Timber.d("Creating Trip")
        job = CoroutineScope(Dispatchers.IO).launch {
            val reuslt = FirebaseManager.createTrip(trip,currentUserId)

            if(reuslt.isSuccess){
                Timber.d("Trip created id: ${reuslt.data}")
            }
}
    }
}




