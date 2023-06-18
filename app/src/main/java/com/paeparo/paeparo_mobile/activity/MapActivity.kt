package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.widget.SearchView
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.adapter.MapListAdpater
import com.paeparo.paeparo_mobile.databinding.AcitivityMapBinding
import com.paeparo.paeparo_mobile.manager.KakaoRetroFit
import com.paeparo.paeparo_mobile.model.MapViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class MapActivity : AppCompatActivity(), SearchView.OnQueryTextListener, OnMapReadyCallback  {

    enum class ViewMode {
        Search, Map
    }
    lateinit var naverMap : NaverMap
    lateinit var binding: AcitivityMapBinding
    val mapViewModel: MapViewModel by lazy {
        ViewModelProvider(this)[MapViewModel::class.java]
    }
    private var currentViewMode = ViewMode.Map //맵 or 검색창인지 확인하는 변수
    lateinit var mapListAdpater: MapListAdpater
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcitivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind();

        lifecycleScope.launch {
            mapViewModel.kaKaoResponseList.collect {
                mapListAdpater.submitList(it.documents)

            }
        }


        val mIntent = Intent(this@MapActivity, PlanActivity::class.java).apply {
            putExtra("ResultData", "DONE!")
        }
        setResult(RESULT_OK, mIntent)


    }

    private fun bind() {
        with(binding) {
            mapListAdpater = MapListAdpater()
            svMap.setOnQueryTextListener(this@MapActivity)

            svMap.setOnQueryTextFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    // 맵을 보고 있는 화면에서 검색창을 클릭했을 때,
                    switchView(ViewMode.Search)
                } else {
                    // 검색 화면에서 벗어났을 때,
                    switchView(ViewMode.Map)
                }
            }
            // 지도 객체 생성
            val fm = supportFragmentManager

            val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.map_fragment, it).commit()
                }
            //NaverMap 객체 얻어오기
            mapFragment.getMapAsync(this@MapActivity)



            rvMap.adapter = mapListAdpater
            rvMap.layoutManager = LinearLayoutManager(applicationContext)

            switchView(ViewMode.Map)

        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query == null) return false
        mapViewModel.searchQuery(query)
        if(currentViewMode == ViewMode.Map){
            switchView(ViewMode.Search)
        }
        return true;
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(currentViewMode == ViewMode.Map){
            switchView(ViewMode.Search)
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentViewMode == ViewMode.Search) {
            // 검색화면 일 경우,
            switchView(ViewMode.Map)
            return;
        } else if (currentViewMode == ViewMode.Map) {
            // 맵 화면 일 경우, 종료
            super.onBackPressed()
        }
    }

    /**
     * Switch view
     * 맵 또는 검색화면을 보여주는 함수
     * true 일 경우, 맵 | false 일 경우, 검색화면
     * @return true -> false, false -> true
     * @param mapActive
     */
    private fun switchView(mode: ViewMode) {
        if (mode == ViewMode.Map) {
            // 맵 활성화
            binding.mapFragment.visibility = View.VISIBLE
            binding.clMapSearch.visibility = View.INVISIBLE
        } else if (mode == ViewMode.Search) {
            // 검색 화면 활성화
            binding.mapFragment.visibility = View.INVISIBLE
            binding.clMapSearch.visibility = View.VISIBLE
        } else {
            //다른 모드일 경우,
            Timber.e("${mode.name} Mode is Not Exist")
        }
        this.currentViewMode = mode
    }


    override fun onMapReady(p0: NaverMap) {
        naverMap = p0
        Timber.d("naverMap 객체 얻기 성공")
    }


}