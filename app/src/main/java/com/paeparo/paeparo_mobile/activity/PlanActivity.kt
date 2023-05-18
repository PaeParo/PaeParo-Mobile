package com.paeparo.paeparo_mobile.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.paeparo.paeparo_mobile.adapter.PlanAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityPlanBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.Event
import com.paeparo.paeparo_mobile.model.Trip
import com.paeparo.paeparo_mobile.util.DateUtil.calPeriod
import com.paeparo.paeparo_mobile.util.DateUtil.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

/*
    일정 세부정보를 일자별로(Day1,Day2) 보는 Activiy
 */
class PlanActivity : AppCompatActivity() {
    var trip: Trip? = null

    private val binding: ActivityPlanBinding by lazy {
        ActivityPlanBinding.inflate(layoutInflater) //viewbinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        trip = getTripFromIntent()
        if (trip == null) finish()
        bind(trip!!)

    }

    private fun bind(trip: Trip) {
        with(binding) {
            tvPlanTitle.text = trip.name
            tvPlanSubtitle.text = trip.region
            vpPlan.adapter = PlanAdapter(this@PlanActivity, groupEventsByDay(trip))

            TabLayoutMediator(tlPlan, vpPlan) { tab, position ->
                tab.text = "DAY ${position + 1}"
            }.attach()
        }
    }

    /**
     * Trip 객체를 Intent에서 가져오는 함수
     *
     * @return Trip : intent에 포함된 trip / null : tripBundle은 존재하나 tripBundle속 getParcelable를 가져올 수 없을 떄
     */
    private fun getTripFromIntent(): Trip? {
        val bundle = intent.getBundleExtra("tripBundle") ?: return null // bundle is Empty
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
        return trip
    }

    /**
     * Trip 객체를 받아서 날짜별 이벤트 목록의 맵으로 반환하는 함수
     * @param trip
     * @return List<Event> :  / null : 이벤트가 존재하지 않는 리스트
     */
    private fun groupEventsByDay(trip: Trip): Map<LocalDate, MutableList<Event>> {
        val map: MutableMap<LocalDate, MutableList<Event>> = mutableMapOf()
        var tripEvents: List<Event> = listOf()

        val startDate = trip.startDate.toLocalDate()
        val EndDate = trip.endDate.toLocalDate()

        // map 초기화 (날짜별 List생성)
        repeat(EndDate.calPeriod(startDate)) {
            val dayEvents: MutableList<Event> = mutableListOf()
            map[startDate.plusDays(it.toLong())] = dayEvents
        }

        //getting Events
        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseManager.getTripEvents(trip.tripId)
            withContext(Dispatchers.Main) {
                tripEvents = result.data!!
            }
        }

        //groping tripEvents
        tripEvents.forEach {
            val eventStartDate = it.startTime.toLocalDate()
            if(!map.containsKey(eventStartDate)){
                map[eventStartDate] = map[eventStartDate]!!.apply{
                    this.add(it)
                }
            }
        }

        return map
    }
}
