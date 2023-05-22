package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.databinding.FragmentPlanDetailsBinding
import com.paeparo.paeparo_mobile.databinding.FragmentPlanLocationBinding

class PlanLocationFragment : Fragment() {
    private var _binding: FragmentPlanLocationBinding? = null
    private val binding get() = _binding!!

    /*
        TODO(석민재)
        1. XML 작성
        2. 위도 경도
     */



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlanLocationBinding.inflate(inflater, container, false) //set binding
        val parentActivity = activity as PlanGenerateActivity
        bind(parentActivity)
        return binding.root
    }

    private fun bind(parentActivity: PlanGenerateActivity) {
        with(binding){
            btnPlanLocation.setOnClickListener{
                parentActivity.binding.vpPlanGenerate.currentItem++
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}