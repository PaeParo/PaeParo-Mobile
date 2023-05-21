package com.paeparo.paeparo_mobile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paeparo.paeparo_mobile.fragment.PlanCompanionFragment
import com.paeparo.paeparo_mobile.fragment.PlanCalenderFragment
import com.paeparo.paeparo_mobile.fragment.PlanLocationFragment

class PlanGenerateAdapter(fa : FragmentActivity) : FragmentStateAdapter(fa){

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> { PlanCalenderFragment() }
            1 -> { PlanCompanionFragment() }
            else -> { PlanLocationFragment() }

        }
    }

}