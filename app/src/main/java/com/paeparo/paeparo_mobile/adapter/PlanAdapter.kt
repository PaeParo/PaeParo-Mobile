package com.paeparo.paeparo_mobile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paeparo.paeparo_mobile.fragment.MyHomeFragment
import timber.log.Timber

/*
PlanActivity의 ViewPager용 어뎁터
 */
class PlanAdapter(fa: FragmentActivity, nextInt: Int) : FragmentStateAdapter(fa) {

    var i = 0
    init {
        i = nextInt
    }
    override fun getItemCount(): Int = i.also{
        Timber.d("getItemCount : ${it.toString()}")
    }

    override fun createFragment(position: Int): Fragment {
        return MyHomeFragment().also{
            Timber.d("createFragment : ${it.hashCode()}")
        }
    }
}