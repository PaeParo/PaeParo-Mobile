package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.adapter.PlanAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityPlanBinding
import com.paeparo.paeparo_mobile.fragment.PlanInfoFragment
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


interface EditModeable {

    var state: PlanActivity.MODE
    fun changeMode(state: PlanActivity.MODE)
}

class PlanActivity(override var state: MODE = MODE.DISPLAY) : AppCompatActivity(), EditModeable {
    enum class MODE {
        DISPLAY, EDIT,
    }

    private lateinit var eventsByDate: Map<LocalDate, MutableList<Event>>
    private val planAdapter by lazy {
        PlanAdapter(this@PlanActivity, eventsByDate, state)
    }
    private val binding: ActivityPlanBinding by lazy {
        ActivityPlanBinding.inflate(layoutInflater) //viewbinding
    }
    private val currentFragent: PlanInfoFragment
        get() = planAdapter.planInfoFragments[binding.vpPlan.currentItem]

    private lateinit var launcher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val trip = getTripFromIntent()

        if (trip == null) finish()
        eventsByDate = groupEventsByDay(trip!!)
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            MapResultCallBack()
        )


        bind(trip)

    }

    private fun bind(trip: Trip) {
        with(binding) {
            tvPlanTitle.text = trip.name
            tvPlanSubtitle.text = trip.region
            vpPlan.adapter = planAdapter
            vpPlan.offscreenPageLimit = 15

            TabLayoutMediator(tlPlan, vpPlan) { tab, position ->
                tab.text = "DAY ${position + 1}"
            }.attach()

            btnPlanFab.setOnClickListener {
                //createEvent()
                val mIntent = Intent(this@PlanActivity, MapActivity::class.java)
                launcher.launch(mIntent)
            }

            btnPlanEdit.setOnClickListener {
                state = if (state == MODE.DISPLAY) MODE.EDIT else MODE.DISPLAY
                changeMode(state)
            }
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
            map[startDate.plusDays(it.toLong())] = mutableListOf()
        }

        //getting Events
        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseManager.getTripEvents(trip.tripId)
            withContext(Dispatchers.Main) {
                tripEvents = result.data!!
            }
        }

        tripEvents.forEach { event ->
            val eventStartDate = event.startTime.toLocalDate()
            map.computeIfAbsent(eventStartDate) { mutableListOf() }.add(event)
        }

        return map
    }

    override fun changeMode(state: PlanActivity.MODE) {
        val (imageResource, isUserInputEnabled) = when (state) {
            MODE.DISPLAY -> R.drawable.ic_edit to true
            MODE.EDIT -> R.drawable.ic_close to false
        }

        binding.btnPlanEdit.setImageResource(imageResource)
        binding.vpPlan.isUserInputEnabled = isUserInputEnabled
        currentFragent.changeMode(state)
    }

    fun createEvent() {
        // getting List
        val eventList =
            currentFragent.planInfoAdapter.currentList
        // update List
        val newList = eventList.toMutableList()
        newList.add(Event(name = "앙기모띠"))

        // update PlanInfoViewPager
        currentFragent.planInfoAdapter.applyListUpdate(
            newList
        )
    }

    inner class MapResultCallBack : ActivityResultCallback<ActivityResult> {
        override fun onActivityResult(result: ActivityResult) {

            val str = when (result.resultCode) {
                RESULT_OK -> "RESULT_OK"
                RESULT_CANCELED -> "RESULT_CANCELED"
                else -> "RESULT_ELSE"
            }
            val str2 = result.data!!.getStringExtra("ResultData")

            Toast.makeText(
                this@PlanActivity,
                "result Code : $str\t result Data : str2 ",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}


