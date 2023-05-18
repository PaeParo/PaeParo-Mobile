package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paeparo.paeparo_mobile.R

class PlanLocationFragment : Fragment() {

    /*
        TODO(석민재)
        1. XML 작성
        2. 위도 경도 
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plan_location, container, false)
    }


}