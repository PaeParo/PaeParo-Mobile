package com.paeparo.paeparo_mobile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paeparo.paeparo_mobile.activity.EditModeable
import com.paeparo.paeparo_mobile.activity.MODE
import com.paeparo.paeparo_mobile.fragment.PlanInfoFragment
import com.paeparo.paeparo_mobile.model.Event
import java.time.LocalDate

/*
PlanActivity의 ViewPager용 어뎁터
 */
class PlanAdapter(fa: FragmentActivity, private val map: Map<LocalDate, MutableList<Event>>) : FragmentStateAdapter(fa),EditModeable{
    var planInfoFragments = mutableListOf<PlanInfoFragment>()
    override fun getItemCount(): Int = map.keys.size

    override fun createFragment(position: Int): Fragment {
        val list = map.values.toList()
        return PlanInfoFragment(list[position]).also{
            planInfoFragments.add(it)
        }
    }

    override fun changeMode() {
        for(activity in planInfoFragments){
            activity.changeMode()
        }
    }


}

