package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.widget.SearchView
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.adapter.MapListAdpater
import com.paeparo.paeparo_mobile.databinding.AcitivityMapBinding
import com.paeparo.paeparo_mobile.model.KakaoMapModel.KaKaoResponse
import com.paeparo.paeparo_mobile.model.MapViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class MapActivity : AppCompatActivity(), SearchView.OnQueryTextListener, OnMapReadyCallback {

    enum class ViewMode {
        // 현재 모드를 담는 ENUM
        // Seach : 검색 화면
        // Map : 맵 화면
        Search, Map
    }

    lateinit var binding: AcitivityMapBinding
    val mapViewModel: MapViewModel by lazy {
        ViewModelProvider(this)[MapViewModel::class.java]
    }

    private var currentViewMode = ViewMode.Map //맵 or 검색창인지 확인하는 변수
    lateinit var mapListAdpater: MapListAdpater
    lateinit var naverMap: NaverMap
    private val currentMarkerList = mutableListOf<Marker>() // 맵에 현재 표시된 마커를 담는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcitivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind();

        lifecycleScope.launch {
            mapViewModel.kaKaoResponseList.collect {
                mapListAdpater.submitList(it.documents)
                removeAllMarks(it.documents)
                showMarkers(it.documents)
            }
        }


        val mIntent = Intent(this@MapActivity, PlanActivity::class.java).apply {
            putExtra("ResultData", "DONE!")
        }
        setResult(RESULT_OK, mIntent)


    }

    private fun showMarkers(documents: List<KaKaoResponse.Document?>?) {
        if (documents == null) return

        documents.forEach {
            if (it == null) return@forEach

            // 마커 생성
            val marker = Marker()
            marker.position = LatLng(it.y!!.toDouble(), it.x!!.toDouble())
            marker.map = naverMap
            marker.setOnClickListener { o->
                showMarkerInfo(it)
                true
            }

            currentMarkerList.add(marker)
        }


    }

    /*
    현재 지도에 표시된 마커를 없애는 함수
     */
    private fun removeAllMarks(documents: List<KaKaoResponse.Document?>?) {
        currentMarkerList.forEach {
            it.map = null //마커 삭제
        }
        currentMarkerList.clear() // 마커 리스트 비우기
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
            rvMap.layoutManager = LinearLayoutManager(this@MapActivity)


            switchView(ViewMode.Map)

        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query == null) return false
        mapViewModel.searchQuery(query)
        if (currentViewMode == ViewMode.Map) {
            switchView(ViewMode.Search)
        }
        binding.svMap.clearFocus()
        return true;
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (currentViewMode == ViewMode.Map) {
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
            binding.llMapLocalInfo.visibility = View.GONE
        } else {
            //다른 모드일 경우,
            Timber.e("${mode.name} Mode is Not Exist")
        }
        this.currentViewMode = mode
    }

    /**
     * Show marker info
     * 화면을 Map 화면으로 전환한 후, 해당 장소에 대한 정보를 보여주는 LinearLayout을 보여줌
     * @param item
     */
    public fun showMarkerInfo(item: KaKaoResponse.Document) {

        switchView(ViewMode.Map)
        // 카메라 이동
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(item.y!!.toDouble(), item.x!!.toDouble()))
            .animate(CameraAnimation.Easing) // 부드럽게 이동
        naverMap.moveCamera(cameraUpdate)


        with(binding) {
            svMap.clearFocus()
            llMapLocalInfo.visibility = View.VISIBLE
            tvMarkerInfoAddressName.text = item.addressName

            // 카테고리 정규화
            // 서비스,산업 \u003e 제조업 \u003e 전기,전자 \u003e 전기자재,부품의 경우 -> "전기자재,부품" 만 추출
            var category: String? = null
            try {
                category = Regex("(?<=\\u003e\\s)[^\\u003e]+(?=\\z)").find(item.categoryName!!)!!.value
            }catch (e : Exception){
                // 자
                Timber.e("카테고리가 NULL 임")
            }

            tvMarkerInfoCategoryName.text = category

            tvMarkerInfoPlaceName.text = item.placeName
            btnMarkerInfoAdd.setOnClickListener{
                createEvents()
            }
        }
    }

    /*

     */
    private fun createEvents(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("일정을 언제 추가할까요?")
            .setView(View.inflate(this,R.layout.item_create_event,null))
        builder.setPositiveButton("확인",null)
        builder.setNegativeButton("취소",null)
        builder.show()
    }

    override fun onMapReady(p0: NaverMap) {
        naverMap = p0;
    }

}