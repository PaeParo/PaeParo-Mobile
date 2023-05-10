package com.paeparo.paeparo_mobile.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.FragmentPlanEmptyBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.launch


class PlanFragment : Fragment() {
    private var _binding: FragmentPlanEmptyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanEmptyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val userTripsResult = FirebaseManager.getUserTrips(view.context.getPaeParo().userId)

            if (userTripsResult.isSuccess) {
                val userTrips = userTripsResult.data
                if (userTrips!!.isEmpty()) { // 내가 속한 여행이 없을 경우
                    binding.layoutPlanEmptyView.root.visibility =
                        View.VISIBLE
                    binding.layoutPlanEmptyTripList.root.visibility =
                        View.GONE
                } else { // 내가 속한 여행이 있을 경우
                    binding.layoutPlanEmptyView.root.visibility =
                        View.GONE
                    binding.layoutPlanEmptyTripList.root.visibility =
                        View.VISIBLE
                }
            } else {
                Toast.makeText(view.context, "여행 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.layoutPlanEmptyView.root.findViewById<LinearLayoutCompat>(R.id.btn_plan_empty_create_trip)
            .setOnClickListener {
                val intent = Intent(context, PlanGenerateActivity::class.java)
                startActivity(intent)
            }

        binding.layoutPlanEmptyTripList.root.findViewById<LinearLayoutCompat>(R.id.btn_plan_empty_create_trip)
            .setOnClickListener {
                val intent = Intent(context, PlanGenerateActivity::class.java)
                startActivity(intent)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}