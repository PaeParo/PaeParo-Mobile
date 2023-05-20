package com.paeparo.paeparo_mobile.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.applikeysolutions.cosmocalendar.model.Day
import com.google.android.material.snackbar.Snackbar
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.databinding.FragmentPlanCalenderBinding

class PlanDateFragment : Fragment() {
    private var _binding: FragmentPlanCalenderBinding? = null
    private val binding get() = _binding!!
    private var dates: List<Day> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanCalenderBinding.inflate(inflater, container, false) //set binding
        val viewPager2 = (activity as PlanGenerateActivity).binding.vpPlanGenerate
        bind(viewPager2)
        // 버튼

        return binding.root
    }

    private fun bind(viewPager2: ViewPager2) {

        with(binding) {
            btnCalNext.setOnClickListener {
                // 날짜를 고르지 않았을 경우,
                if (dates.isEmpty()) {
                    Snackbar.make(it, "여행 날짜를 선택해주세요", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // 다음페이지로 이동
                viewPager2.currentItem++
            }
            cvPlanGenerate.isShowDaysOfWeekTitle = false

        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}