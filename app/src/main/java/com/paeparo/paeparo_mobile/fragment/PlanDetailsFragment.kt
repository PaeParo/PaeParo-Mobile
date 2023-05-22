package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.databinding.FragmentPlanCalenderBinding
import com.paeparo.paeparo_mobile.databinding.FragmentPlanDetailsBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class PlanDetailsFragment : Fragment() {
    private var _binding: FragmentPlanDetailsBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlanDetailsBinding.inflate(inflater, container, false) //set binding

        val parentActivity = activity as PlanGenerateActivity

        bind(parentActivity)

        return binding.root
    }

    private fun bind(parentActivity : PlanGenerateActivity) {
        with(binding){

            btnPlanDetails.setOnClickListener{
                with(parentActivity.trip) {
                    name = etTvPlanDetailTitle.text.toString()
                    budget = etPlanDetailBudget.text.toString().toInt()
                    // 임시값
                    region = "테스트 용 : PlanDetailFragment에서 생성됨."
                    status = Trip.TripStatus.PLANNING
                }

                parentActivity.createTrip()
                parentActivity.finish()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}