package com.paeparo.paeparo_mobile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paeparo.paeparo_mobile.activity.EditModeable
import com.paeparo.paeparo_mobile.activity.PlanActivity
import com.paeparo.paeparo_mobile.fragment.PlanInfoFragment
import com.paeparo.paeparo_mobile.model.Event
import java.time.LocalDate

/*
PlanActivity의 ViewPager용 어뎁터
 */
class PlanAdapter(fa: FragmentActivity, private val map: Map<LocalDate, MutableList<Event>>,
                  override var state: PlanActivity.MODE
) : FragmentStateAdapter(fa),EditModeable{
    var planInfoFragments = mutableListOf<PlanInfoFragment>()
    override fun getItemCount(): Int = map.keys.size

    override fun createFragment(position: Int): Fragment {
        val list = map.values.toList()
        return PlanInfoFragment(list[position],state).also{
            planInfoFragments.add(it)
        }
    }

    override fun changeMode(state: PlanActivity.MODE) {
        this.state = state
        for(activity in planInfoFragments){
            activity.changeMode(state)
        }
    }


}

