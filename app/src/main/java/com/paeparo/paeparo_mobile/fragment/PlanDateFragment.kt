package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.FragmentPlanDateBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class PlanDateFragment : Fragment() {
    private var _binding: FragmentPlanDateBinding? = null
    private val binding get() = _binding!!
    private val isChecked = false
    private lateinit var startDate : CalendarDay
    private lateinit var endDate : CalendarDay
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlanDateBinding.inflate(inflater, container, false) //set binding

        binding.btnFragmentname.apply {
            setOnClickListener {
                val vp = activity?.findViewById<ViewPager2>(R.id.vp_plan_generate)
                vp?.setCurrentItem(1, true)
            }
        }

        binding.cvPlan.apply {
            //TODO: 여행 최대날짜
            //TODO: 여행 최소날짜
            //TODO: 날짜 하얀색
            //TODO: 과거 날짜 클릭 금지



            //TODO: 날짜 가져오기
            setOnRangeSelectedListener { widget, dates ->
                // 시작 날짜와 마지막 날짜 가져오기
                startDate = dates[0]
                endDate = dates[dates.size - 1]
                val message = "Selected range: $startDate - $endDate"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            }
        }



        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}