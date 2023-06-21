package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.adapter.PlanInfoAdapter
import com.paeparo.paeparo_mobile.callback.PlanItemTouchHelperCallback
import com.paeparo.paeparo_mobile.databinding.FragmentPlanInfoBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.PlanViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

/*
일자별 Event를 표시해주는 RecyclerView
 */

class PlanInfoFragment(private val localDate: LocalDate) : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var model: PlanViewModel
    private lateinit var itemTouchHelperCallback: ItemTouchHelper
    private var _binding: FragmentPlanInfoBinding? = null
    private val binding get() = _binding!!

    lateinit var planInfoAdapter: PlanInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanInfoBinding.inflate(inflater, container, false)
        model = ViewModelProvider(requireActivity()).get(PlanViewModel::class.java)
        bind(binding)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    model.eventMap.value[localDate]?.collect {
                        Timber.d("PlanInfoActivity -> eventList collected")
                        planInfoAdapter.submitList(it)
                    }
                }
                launch {
                    model.viewMode.collect{
                        when(it) {
                            PlanViewModel.MODE.VIEW -> itemTouchHelperCallback.attachToRecyclerView(null) // 슬라이드 방지
                            PlanViewModel.MODE.EDIT -> itemTouchHelperCallback.attachToRecyclerView(recyclerView) // 슬라이드 가능
                            else -> {
//                                Toast.makeText(context, "PlanInfoActivity -> viewModeChanged() : $it", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun bind(binding: FragmentPlanInfoBinding) {
        with(binding) {
            recyclerView = rvList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                planInfoAdapter = PlanInfoAdapter(model,localDate)
                adapter = planInfoAdapter
            }

            planInfoAdapter.submitList(model.eventMap.value[localDate]?.value)
            itemTouchHelperCallback = ItemTouchHelper(PlanItemTouchHelperCallback(rvList))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}