package com.paeparo.paeparo_mobile.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.paeparo.paeparo_mobile.adapter.PlanAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityPlanBinding
import com.paeparo.paeparo_mobile.model.Trip
import com.paeparo.paeparo_mobile.model.TripPlan
import timber.log.Timber

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

        bind()
        Timber.d(getTrip().toString())

        TripPlan(getTrip()!!)
    }
    private fun bind() {
        binding.vpPlan.adapter = PlanAdapter(this)

        with(binding) {
            TabLayoutMediator(tlPlan, vpPlan) { tab, position ->
                tab.text = "tab"
            }
        }
    }

    // intent에서 Trip 추출 deprecated되어서 sdk 버전에 따라 처리
    private fun getTrip(): Trip? {
        val bundle = intent.getBundleExtra("tripBundle")
        if (bundle == null) {
            // 번들이 없을 경우,
            Timber.e("(getTrip) TripBundle is Null")
            return null
        }
        // Android 13
        val trip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(
                "trip",
                Trip::class.java
            )

        }
        // Android 13 보다 낮은 버전
        else {
            bundle.getParcelable<Trip>("trip")
        }
        return trip // 애초에 bundle이있는데 trip이 null 일 수 없음.
    }

}
