package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.activity.EditModeable
import com.paeparo.paeparo_mobile.activity.MODE
import com.paeparo.paeparo_mobile.adapter.PlanInfoAdapter
import com.paeparo.paeparo_mobile.callback.PlanItemTouchHelperCallback
import com.paeparo.paeparo_mobile.databinding.FragmentPlanInfoBinding
import com.paeparo.paeparo_mobile.model.Event

/*
일자별 Event를 표시해주는 RecyclerView
 */

class PlanInfoFragment(val eventList: MutableList<Event>, override var state: MODE) : Fragment(), EditModeable{

    private lateinit var recyclerView : RecyclerView
    private lateinit var itemTouchHelperCallback : ItemTouchHelper
    private var _binding: FragmentPlanInfoBinding? = null
    private val binding get() = _binding!!

    val planInfoAdapter: PlanInfoAdapter by lazy {
        PlanInfoAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanInfoBinding.inflate(inflater, container, false)
        bind(binding)

        return binding.root


    }

    private fun bind(binding: FragmentPlanInfoBinding) {
        with(binding) {
            recyclerView = rvList.apply{
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = planInfoAdapter
            }

            planInfoAdapter.submitList(eventList)
            itemTouchHelperCallback = ItemTouchHelper(PlanItemTouchHelperCallback(rvList))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun changeMode() {
        state = if(state == MODE.DISPLAY) MODE.EDIT else MODE.DISPLAY // state 전환

        when (state) {
            MODE.DISPLAY -> itemTouchHelperCallback.attachToRecyclerView(null) // 슬라이드 방지
            MODE.EDIT -> itemTouchHelperCallback.attachToRecyclerView(recyclerView) // 슬라이드 가능
        }
    }

}