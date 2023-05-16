package com.paeparo.paeparo_mobile.fragment

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var invitationListDialog: BottomSheetDialog

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

        dialogInvitationListBinding = DialogInvitationListBinding.inflate(layoutInflater)

        setupTripViewModel()

        setupAdapter()

        setupSwipeGesture()

        tripViewModel.loadTrips(requireContext().getPaeParo().userId)
    }

    /**
     * TripVieModel 초기화 함수
     */
    private fun setupTripViewModel() {
        // TripViewModel 초기화
        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]

        // TripViewModel의 loading 변경사항 확인
        tripViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.clTripInvitation.visibility = View.INVISIBLE
                binding.ciTripLoading.visibility = View.VISIBLE
                binding.layoutTripNotExists.root.visibility =
                    View.INVISIBLE
                binding.layoutTripExists.root.visibility =
                    View.INVISIBLE
            } else {
                binding.ciTripLoading.visibility =
                    View.GONE
            }
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
                invitationListDialog.hide()
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
    }

    /**
     * TripAdapter, InvitationAdapter 및 Dialog 초기화 함수
     */
    private fun setupAdapter() {
        // TripAdapter 초기화
        tripAdapter = TripAdapter()
        binding.layoutTripExists.rvTripExistsTripList.layoutManager =
            LinearLayoutManager(context)
        binding.layoutTripExists.rvTripExistsTripList.adapter = tripAdapter

        // InvitationAdapter 초기화
        invitationAdapter = InvitationAdapter(
            { trip -> tripViewModel.acceptInvitation(trip, requireContext().getPaeParo().userId) },
            { trip -> tripViewModel.declineInvitation(trip, requireContext().getPaeParo().userId) }
        )
        dialogInvitationListBinding.rvInvitationList.layoutManager = LinearLayoutManager(context)
        dialogInvitationListBinding.rvInvitationList.adapter = invitationAdapter

        // InvitationListDialog 초기화
        invitationListDialog = BottomSheetDialog(requireContext())
        invitationListDialog.setContentView(dialogInvitationListBinding.root)
        binding.clTripInvitation.setOnClickListener {
            invitationListDialog.show()
        }
    }

    /**
     * Invitation recycler view의 SwipeGesture 설정 함수
     */
    private fun setupSwipeGesture() {
        val invitationItemTouchHelper = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val trip = invitationAdapter.currentList[position]

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        tripViewModel.declineInvitation(trip, requireContext().getPaeParo().userId)
                    }

                    ItemTouchHelper.RIGHT -> {
                        tripViewModel.acceptInvitation(trip, requireContext().getPaeParo().userId)
                    }
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView

                if (dX > 0) { // 우측으로 스와이프할 경우
                    // 배경색을 파란 계열로 설정
                    val background = RectF(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        dX,
                        itemView.bottom.toFloat()
                    )
                    val paint = Paint()
                    paint.color = Color.parseColor("#87CEFA")
                    canvas.drawRect(background, paint)

                    // 텍스트 설정 및 실제 차지하는 크기 계산
                    val text = "수락"
                    paint.color = Color.WHITE
                    paint.textSize = 48f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    val textWidth = paint.measureText(text)

                    if (dX > textWidth) { // 텍스트보다 배경 너비가 더 길어질 경우에만 텍스트 표시
                        val textX = (itemView.left + ((dX - textWidth) / 2))
                        val textY =
                            itemView.top + ((itemView.bottom - itemView.top) / 2) + (paint.textSize / 2)
                        canvas.drawText(text, textX, textY, paint)
                    }
                } else if (dX < 0) { // 좌측으로 스와이프할 경우
                    // 배경색을 파란 계열로 설정
                    val background = RectF(
                        itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    val paint = Paint()
                    paint.color = Color.parseColor("#D32F2F")
                    canvas.drawRect(background, paint)

                    // 텍스트 설정 및 실제 차지하는 크기 계산
                    val text = "거절"
                    paint.color = Color.WHITE
                    paint.textSize = 48f
                    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    val textWidth = paint.measureText(text)

                    if (-dX > textWidth) { // 텍스트보다 배경 너비가 더 길어질 경우에만 텍스트 표시
                        val textX = (itemView.right + dX + ((-dX - textWidth) / 2))
                        val textY =
                            itemView.top + ((itemView.bottom - itemView.top) / 2) + (paint.textSize / 2)
                        canvas.drawText(text, textX, textY, paint)
                    }
                }
            }
        }

        ItemTouchHelper(invitationItemTouchHelper).attachToRecyclerView(dialogInvitationListBinding.rvInvitationList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}