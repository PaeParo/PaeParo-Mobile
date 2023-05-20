package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paeparo.paeparo_mobile.databinding.FragmentPlanStyleBinding

class PlanStyleFragment : Fragment() {
    private var _binding: FragmentPlanStyleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanStyleBinding.inflate(inflater, container, false) //set binding
        return binding.root
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}