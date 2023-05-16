package com.paeparo.paeparo_mobile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paeparo.paeparo_mobile.fragment.MyHomeFragment
/*
PlanActivity의 ViewPager용 어뎁터
 */
class PlanAdapter(fa : FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MyHomeFragment()
            1 -> MyHomeFragment()
            else -> MyHomeFragment()

        }
    }
}