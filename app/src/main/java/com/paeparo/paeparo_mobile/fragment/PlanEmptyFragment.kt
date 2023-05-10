package com.paeparo.paeparo_mobile.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.launch


class PlanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan_empty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val userTripsResult = FirebaseManager.getUserTrips(view.context.getPaeParo().userId)

            if (userTripsResult.isSuccess) {
                val userTrips = userTripsResult.data
                if (userTrips!!.isEmpty()) { // 내가 속한 여행이 없을 경우
                    view.findViewById<LinearLayoutCompat>(R.id.ll_plan_empty_view).visibility =
                        View.VISIBLE
                    view.findViewById<ConstraintLayout>(R.id.cl_plan_empty_trip_list).visibility =
                        View.GONE
                } else { // 내가 속한 여행이 있을 경우
                    view.findViewById<LinearLayoutCompat>(R.id.ll_plan_empty_view).visibility =
                        View.GONE
                    view.findViewById<ConstraintLayout>(R.id.cl_plan_empty_trip_list).visibility =
                        View.VISIBLE
                }
            } else {
                Toast.makeText(view.context, "여행 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<LinearLayoutCompat>(R.id.btn_plan_empty_create_trip).setOnClickListener {
            val intent = Intent(context, PlanGenerateActivity::class.java)
            startActivity(intent)
        }
    }
}