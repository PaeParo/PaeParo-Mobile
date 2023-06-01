package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.databinding.ItemAddLocationBinding
import com.paeparo.paeparo_mobile.databinding.FragmentPlanLocationBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class PlanLocationFragment : Fragment() {
    private var _binding: FragmentPlanLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanLocationBinding.inflate(inflater, container, false) //set binding
        val parentActivity = activity as PlanGenerateActivity
        bind(parentActivity)

        val slidePanel = binding.planLocationMainFrame
        slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

        return binding.root
    }

    private fun bind(parentActivity: PlanGenerateActivity) {
        with(binding) {
            btnPlanLocation.setOnClickListener {
                parentActivity.binding.vpPlanGenerate.currentItem++
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
        TODO(석민재)
        //지역 api에 넘겨서 위도,경도 겸색 retrofit2
        // 해당 위도,경도 통해서 plan_location_map 업데이트하기
        //키보드관련 수정
     */


    private lateinit var locationAdapter: LocationAdapter

    private var locationList: MutableList<String> = mutableListOf(
        "서울특별시",
        "부산광역시",
        "경기도",
        "강원도",
        "서울특별시1",
        "부산광역시1",
        "경기도1",
        "강원도1",
        "서울특별시2",
        "부산광역시2",
        "경기도2",
        "강원도2",
        "서울특별시3",
        "부산광역시3",
        "경기도3",
        "강원도3"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parentActivity = activity as PlanGenerateActivity
        bind(parentActivity)

        binding.svPlanLocation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterLocation(newText)
                return true
            }
        })

        binding.svPlanLocation.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val state = binding.planLocationMainFrame.panelState

                if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    binding.planLocationMainFrame.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                }
            } else {
                binding.planLocationMainFrame.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }

        locationAdapter = LocationAdapter(locationList, binding.planLocationMainFrame)
        binding.locationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.locationRecyclerView.adapter = locationAdapter
    }

    private fun filterLocation(query: String) {
        val filteredList = locationList.filter { location ->
            location.contains(query, ignoreCase = true)
        }
        locationAdapter.updateData(filteredList)
    }

    class LocationAdapter(private var locationList: List<String>,
                          private val slidePanel: SlidingUpPanelLayout
                          ) :
        RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

        inner class LocationViewHolder(private val binding: ItemAddLocationBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.btnAddLocation.setOnClickListener {
                    slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

                    //지역 api에 넘겨서 위도,경도 겸색
                    // 해당 위도,경도 통해서 plan_location_map 업데이트하기
//키보드관련 수정
                }
            }
            fun bind(location: String) {
                binding.tvAddLocation.text = location
            }
        }

        fun updateData(newLocationList: List<String>) {
            locationList = newLocationList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemAddLocationBinding.inflate(inflater, parent, false)
            return LocationViewHolder(binding)
        }

        override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
            val location = locationList.get(position)
            holder.bind(location)
        }

        override fun getItemCount(): Int {
            return locationList.size
        }
    }


}
