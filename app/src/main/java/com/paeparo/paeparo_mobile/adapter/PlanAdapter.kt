package com.paeparo.paeparo_mobile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paeparo.paeparo_mobile.fragment.PlanInfoFragment
import com.paeparo.paeparo_mobile.model.PlanViewModel
import java.time.LocalDate

/*
PlanActivity의 ViewPager용 어뎁터
 */
class PlanAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa){
    private lateinit var model : PlanViewModel
    private lateinit var keySet: Set<LocalDate>
    init{
        model = ViewModelProvider(fa).get(PlanViewModel::class.java)
    }

    override fun getItemCount(): Int = model.localDateList.size

    override fun createFragment(position: Int): Fragment {
        return PlanInfoFragment(model.localDateList[position])
    }
}

