package com.paeparo.paeparo_mobile.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import com.google.android.material.snackbar.Snackbar
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.databinding.FragmentPlanDateBinding
import com.prolificinteractive.materialcalendarview.CalendarDay

class PlanDateFragment : Fragment() {
    private var _binding: FragmentPlanDateBinding? = null
    private val binding get() = _binding!!
    private val isChecked = false
    private var dates: List<Day> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanDateBinding.inflate(inflater, container, false) //set binding

        // 버튼
        binding.btnCalNext.apply {
            // 리스너
            setOnClickListener {
                // 날짜를 고르지 않았을 경우,
                if(dates.isEmpty()) {
                    Snackbar.make(it, "여행 날짜를 선택해주세요", Snackbar.LENGTH_SHORT).show();
                    return@setOnClickListener
                }
                // 다음페이지로 이동
                nextPage()
            }
        }

        binding.cvPlan.apply {
            isShowDaysOfWeekTitle = false
            selectionManager = RangeSelectionManager(OnDaySelectedListener {
                if (this.selectedDates.size <= 0) return@OnDaySelectedListener
                Log.d("PlanDateFragment", "btnclicked")
                dates = selectedDays
            })

        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun nextPage(){
        val t = activity as? PlanGenerateActivity
        t?.settestData("dates",dates.toString())
        Log.d("PlanDateFragment","Get Intent dates :"+t?.testDataPrint("dates"))
        val vp = activity?.findViewById<ViewPager2>(R.id.vp_plan_generate)
        vp?.setCurrentItem(1, true)
    }
}