package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.adapter.TripAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.DialogInvitationsBinding
import com.paeparo.paeparo_mobile.databinding.FragmentTripBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.Trip
import kotlinx.coroutines.launch


class TripFragment : Fragment() {
    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clTripInvitation.visibility = View.INVISIBLE
        binding.ciTripLoading.visibility = View.VISIBLE
        binding.layoutTripEmpty.root.visibility =
            View.INVISIBLE
        binding.layoutTripTrips.root.visibility =
            View.INVISIBLE

        lifecycleScope.launch {
            val userTripsResult = FirebaseManager.getUserTrips(view.context.getPaeParo().userId)

            binding.ciTripLoading.visibility = View.GONE

            if (userTripsResult.isSuccess) {
                val userTrips = userTripsResult.data
                if (userTrips!!.first.isEmpty()) { // 내가 속한 여행이 없을 경우
                    binding.layoutTripEmpty.root.visibility =
                        View.VISIBLE
                    binding.layoutTripTrips.root.visibility =
                        View.GONE
                    binding.layoutTripEmpty.root.findViewById<LinearLayoutCompat>(
                        R.id.btn_trip_create_trip
                    )
                        .setOnClickListener {
                            val intent = android.content.Intent(
                                context,
                                com.paeparo.paeparo_mobile.activity.PlanGenerateActivity::class.java
                            )
                            startActivity(intent)
                        }
                } else { // 내가 속한 여행이 있을 경우
                    binding.layoutTripEmpty.root.visibility =
                        View.GONE
                    binding.layoutTripTrips.root.visibility =
                        View.VISIBLE
                    binding.layoutTripTrips.root.findViewById<LinearLayoutCompat>(
                        R.id.btn_trip_create_trip
                    )
                        .setOnClickListener {
                            val intent = android.content.Intent(
                                context,
                                com.paeparo.paeparo_mobile.activity.PlanGenerateActivity::class.java
                            )
                            startActivity(intent)
                        }
                    val tripAdapter = TripAdapter(userTrips.first)
                    binding.layoutTripTrips.rvTripsTripList.adapter = tripAdapter
                    binding.layoutTripTrips.rvTripsTripList.layoutManager =
                        LinearLayoutManager(context)
                }
                if (userTrips.second.isEmpty()) {
                    binding.clTripInvitation.visibility = View.GONE
                } else {
                    binding.clTripInvitation.visibility = View.VISIBLE
                    binding.tvTripInvitationCount.text = userTrips.second.size.toString()
                    binding.clTripInvitation.setOnClickListener {
                        showInvitationDialog(userTrips.second)
                    }
                }
            } else {
                Toast.makeText(view.context, "여행 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showInvitationDialog(invitations: List<Trip>) {
        val invitationsDialog = BottomSheetDialog(requireContext())
        val invitationsDialogBinding = DialogInvitationsBinding.inflate(layoutInflater)
        invitationsDialog.setContentView(invitationsDialogBinding.root)

        invitationsDialogBinding.rvInvitations.layoutManager = LinearLayoutManager(context)
        val invitationsAdapter = TripAdapter(invitations)
        invitationsDialogBinding.rvInvitations.adapter = invitationsAdapter
        invitationsDialogBinding.rvInvitations.layoutManager =
            LinearLayoutManager(context)
        invitationsDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}