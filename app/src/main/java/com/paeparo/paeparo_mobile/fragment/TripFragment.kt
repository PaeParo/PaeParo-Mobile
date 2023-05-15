package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.adapter.InvitationAdapter
import com.paeparo.paeparo_mobile.adapter.TripAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.DialogInvitationListBinding
import com.paeparo.paeparo_mobile.databinding.FragmentTripBinding
import com.paeparo.paeparo_mobile.model.TripViewModel


class TripFragment : Fragment() {
    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!

    /**
     * TripViewModel
     */
    private lateinit var tripViewModel: TripViewModel

    /**
     * TripAdapter
     */
    private lateinit var tripAdapter: TripAdapter

    /**
     * InvitationAdapter
     */
    private lateinit var invitationAdapter: InvitationAdapter

    /**
     * BottomSheetDialog for invitation list
     */
    private lateinit var invitationsDialog: BottomSheetDialog

    /**
     * DialogInvitationListBinding
     */
    private lateinit var dialogInvitationListBinding: DialogInvitationListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 초기화
        binding.clTripInvitation.visibility = View.INVISIBLE
        binding.ciTripLoading.visibility = View.VISIBLE
        binding.layoutTripNotExists.root.visibility =
            View.INVISIBLE
        binding.layoutTripExists.root.visibility =
            View.INVISIBLE

        // TripViewModel 초기화
        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]

        // TripAdapter, InvitationsAdapter/Dialog 초기화
        tripAdapter = TripAdapter()
        binding.layoutTripExists.rvTripExistsTripList.layoutManager =
            LinearLayoutManager(context)
        binding.layoutTripExists.rvTripExistsTripList.adapter = tripAdapter

        invitationsDialog = BottomSheetDialog(requireContext())
        invitationAdapter = InvitationAdapter(
            { trip -> tripViewModel.acceptInvitation(trip, requireContext().getPaeParo().userId) },
            { trip -> tripViewModel.declineInvitation(trip, requireContext().getPaeParo().userId) }
        )
        dialogInvitationListBinding = DialogInvitationListBinding.inflate(layoutInflater)
        dialogInvitationListBinding.rvInvitationList.layoutManager = LinearLayoutManager(context)
        dialogInvitationListBinding.rvInvitationList.adapter = invitationAdapter
        invitationsDialog.setContentView(dialogInvitationListBinding.root)
        binding.clTripInvitation.setOnClickListener {
            invitationsDialog.show()
        }

        // TripViewModel의 trips 변경사항 확인
        tripViewModel.tripList.observe(viewLifecycleOwner) { trips ->
            if (trips.isEmpty()) { // 내가 속한 여행이 없을 경우
                binding.layoutTripNotExists.root.visibility =
                    View.VISIBLE
                binding.layoutTripExists.root.visibility =
                    View.GONE
                binding.layoutTripNotExists.root.findViewById<LinearLayoutCompat>(
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
                binding.layoutTripNotExists.root.visibility =
                    View.GONE
                binding.layoutTripExists.root.visibility =
                    View.VISIBLE
                binding.layoutTripExists.root.findViewById<LinearLayoutCompat>(
                    R.id.btn_trip_create_trip
                )
                    .setOnClickListener {
                        val intent = android.content.Intent(
                            context,
                            com.paeparo.paeparo_mobile.activity.PlanGenerateActivity::class.java
                        )
                        startActivity(intent)
                    }
                tripAdapter.updateTripList(trips)
            }
        }

        // TripViewModel의 invitations 변경사항 확인
        tripViewModel.invitationList.observe(viewLifecycleOwner) { invitations ->
            if (invitations.isEmpty()) { // 초대받은 여행이 없을 경우
                invitationsDialog.hide()
                binding.clTripInvitation.visibility = View.GONE
            } else { // 초대받은 여행이 있을 경우
                binding.clTripInvitation.visibility = View.VISIBLE
                binding.tvTripInvitationCount.text = invitations.size.toString()
            }
            invitationAdapter.updateInvitationList(invitations)
        }

        tripViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, "여행 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }

        tripViewModel.loadTrips(requireContext().getPaeParo().userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}