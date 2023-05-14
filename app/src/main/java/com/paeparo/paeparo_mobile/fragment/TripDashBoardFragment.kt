package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.paeparo.paeparo_mobile.R
import kotlin.math.roundToInt

/**
 * Trip dash board fragment
 *@
 * @TODO 사용하지 않음. 삭제 에정
 */
//class TripDashBoardFragment : Fragment() {
//    private lateinit var binding: FragmentTripDashboardBinding
//    private val tabtitles = mutableMapOf(
//        "Home" to R.drawable.ic_myhome_comment,
//        "Call" to R.drawable.ic_myhome_faq,
//        "Chat" to R.drawable.ic_myhome_comment,)
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentTripDashboardBinding.inflate(layoutInflater)
//        setTabLayOutWithViewPager()
//        return binding.root
//    }
//
//    private fun setTabLayOutWithViewPager() {
//        val titles = ArrayList(tabtitles.keys)
//        binding.tripVp2.adapter = TripDashBoardPagerAdapter(this)
//        TabLayoutMediator(binding.tripTl,binding.tripVp2){ tab, position ->
//            tab.text = titles[position]
//
//        }.attach()
//
//        tabtitles.values.forEachIndexed{ index, imageResId ->
//            val textView = LayoutInflater.from(requireContext()).inflate(R.layout.trip_tab_title,null)
//            as TextView
//            textView.setCompoundDrawablesWithIntrinsicBounds(imageResId,0,0,0)
//            textView.compoundDrawablePadding = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics
//            ).roundToInt()
//            binding.tripTl.getTabAt(index)?.customView = textView
//        }
//    }
//}