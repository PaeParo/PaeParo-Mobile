package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.databinding.FragmentPlanLocationBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.paeparo.paeparo_mobile.activity.MapActivity
import com.paeparo.paeparo_mobile.adapter.LocationAdapter
import com.paeparo.paeparo_mobile.model.GeocodeModel
import com.paeparo.paeparo_mobile.model.NaverResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class PlanLocationFragment : Fragment(), SearchView.OnQueryTextListener, OnMapReadyCallback {
    private var _binding: FragmentPlanLocationBinding? = null
    private val binding get() = _binding!!

    val mapViewModel: GeocodeModel by lazy {
        ViewModelProvider(this)[GeocodeModel::class.java]
    }

    lateinit var locationAdapter: LocationAdapter
    lateinit var naverMap: NaverMap
    private lateinit var slidePanel: SlidingUpPanelLayout
    private val currentMarkerList = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanLocationBinding.inflate(inflater, container, false) //set binding
        val parentActivity = activity as PlanGenerateActivity
        bind(parentActivity)

        lifecycleScope.launch {
            mapViewModel.NaverResponseList.collect {
                locationAdapter.submitList(it.addresses)
                removeAllMarks(it.addresses)
                showMarkers(it.addresses)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showMarkers(documents: List<NaverResponse.Addresse?>?) {
        if (documents == null) return

        documents.forEach {
            if (it == null) return@forEach

            // 마커 생성
            val marker = Marker()
            marker.position = LatLng(it.y!!.toDouble(), it.x!!.toDouble())
            marker.map = naverMap
            marker.setOnClickListener { o ->
                showMarkerInfo(it)
                true
            }

            currentMarkerList.add(marker)
        }


    }

    private fun removeAllMarks(documents: List<NaverResponse.Addresse?>?) {
        currentMarkerList.forEach {
            it.map = null //마커 삭제
        }
        currentMarkerList.clear() // 마커 리스트 비우기
    }

    private fun bind(parentActivity: PlanGenerateActivity) {
        with(binding) {
            locationAdapter= LocationAdapter()
            slidePanel = binding.planLocationMainFrame
            slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

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
            svPlanLocation.setOnQueryTextListener(this@PlanLocationFragment)
            btnPlanLocation.setOnClickListener {
                parentActivity.binding.vpPlanGenerate.currentItem++
            }

            val fm = childFragmentManager
            val mapFragment = fm.findFragmentById(binding.planLocationMap.id) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(binding.planLocationMap.id, it).commit()
                }
            mapFragment.getMapAsync(this@PlanLocationFragment)

            locationRecyclerView.adapter = locationAdapter
            locationRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

        }
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query == null) return false
        mapViewModel.searchQuery(query)

        return true;
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        return true
    }

    public fun showMarkerInfo(item: NaverResponse.Addresse) {

        //지역선택시 slidepanel 내려가도록
        slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(item.y!!.toDouble(), item.x!!.toDouble()))
            .animate(CameraAnimation.Easing) // 부드럽게 이동
        naverMap.moveCamera(cameraUpdate)

    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
//        // 지도 설정
//        naverMap.minZoom = 6.0
//        naverMap.maxZoom = 18.0
//
//        // 마커 추가
//        val marker = Marker()
//        marker.position = LatLng(37.5665, 126.9780)
//        marker.map = naverMap
//
//        // 카메라 이동
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5665, 126.9780))
//        naverMap.moveCamera(cameraUpdate)
    }

}