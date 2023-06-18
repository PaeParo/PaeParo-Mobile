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
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class PlanLocationFragment : Fragment(), OnMapReadyCallback {
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

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(binding.planLocationMap.id) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(binding.planLocationMap.id, it).commit()
            }
        mapFragment.getMapAsync(this)

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
                    binding.planLocationMainFrame.panelState =
                        SlidingUpPanelLayout.PanelState.ANCHORED
                }
            } else {
                binding.planLocationMainFrame.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }

        locationAdapter = LocationAdapter(locationList, binding.planLocationMainFrame)
        binding.locationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.locationRecyclerView.adapter = locationAdapter


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

    private fun filterLocation(query: String) {
        val filteredList = locationList.filter { location ->
            location.contains(query, ignoreCase = true)
        }
        locationAdapter.updateData(filteredList)
    }

    class LocationAdapter(
        private var locationList: List<String>,
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

    override fun onMapReady(naverMap: NaverMap) {
        // 지도 설정
        naverMap.minZoom = 6.0
        naverMap.maxZoom = 18.0

        // 마커 추가
        val marker = Marker()
        marker.position = LatLng(37.5665, 126.9780)
        marker.map = naverMap

        // 카메라 이동
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5665, 126.9780))
        naverMap.moveCamera(cameraUpdate)
    }

}
