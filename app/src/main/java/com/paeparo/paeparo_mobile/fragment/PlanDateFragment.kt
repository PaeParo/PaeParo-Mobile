package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.databinding.FragmentPlanCalenderBinding
import java.util.Date

class PlanDateFragment : Fragment() {
    private var _binding: FragmentPlanCalenderBinding? = null
    private val binding get() = _binding!!
    private var dates: MutableList<Date> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanCalenderBinding.inflate(inflater, container, false) //set binding
        val activity = (activity as PlanGenerateActivity)
        bind(activity)
        // 버튼

        return binding.root
    }

    private fun bind(activity: PlanGenerateActivity) {

        with(binding) {
            // "다음" 버튼
            btnCalNext.setOnClickListener {
                // 날짜를 고르지 않았을 경우,
                if (cvPlanGenerate.selectedDays.isEmpty()) {
                    Snackbar.make(it, "여행 날짜를 선택해주세요", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                dates.add(1, cvPlanGenerate.selectedDates.last().time)

                // 다음페이지로 이동
                activity.binding.vpPlanGenerate.currentItem++

                // 데이터 전달
                activity.trip.startDate = Timestamp(cvPlanGenerate.selectedDates.first().time)
                activity.trip.endDate = Timestamp(cvPlanGenerate.selectedDates.last().time)

            }
            //Cosmo CalenderView
            with(cvPlanGenerate) {
                isShowDaysOfWeekTitle = false

                selectionManager = RangeSelectionManager(OnDaySelectedListener {
                    val dayStart = this.selectedDates.first().time

                    //과거 날짜를 선택 했을 경우
                    if (dayStart.compareTo(Date(System.currentTimeMillis())) <= 0) {
                        clearSelections()
                        Snackbar.make(this, "현재보다 과거의 날짜는 선택할 수 없습니다.", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    // 15일 이상일 경우
                    if (selectedDates.size > 15) {
                        clearSelections()
                        Snackbar.make(this, "15일 이상의 기간은 선택할 수 없습니다.", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                })
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}