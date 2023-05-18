package com.paeparo.paeparo_mobile.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.adapter.PlanInfoAdapter
import com.paeparo.paeparo_mobile.callback.PlanItemTouchHelperCallback
import com.paeparo.paeparo_mobile.databinding.FragmentPlanInfoBinding
import com.paeparo.paeparo_mobile.model.Event

/*
일자별 Event를 표시해주는 RecyclerView
 */
class PlanInfoFragment(val eventList: MutableList<Event>) : Fragment() {
    private var _binding : FragmentPlanInfoBinding? = null
    private val binding get() = _binding!!

    private val planInfoAdapter: PlanInfoAdapter by lazy{
        PlanInfoAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanInfoBinding.inflate(inflater,container,false)

        binding.rvList.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = planInfoAdapter
        }
        planInfoAdapter.submitList(eventList)

        val itemTouchHelperCallback = ItemTouchHelper(PlanItemTouchHelperCallback(binding.rvList))
        itemTouchHelperCallback.attachToRecyclerView(binding.rvList)

        return binding.root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}