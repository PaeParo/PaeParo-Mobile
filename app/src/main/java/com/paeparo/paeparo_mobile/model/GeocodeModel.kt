package com.paeparo.paeparo_mobile.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.paeparo.paeparo_mobile.manager.NaverRetroFit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class GeocodeModel : ViewModel() {
    private val _NaverResponseList =
        MutableSharedFlow<NaverResponse>(); // 내부에서 사용할 리스트

    val NaverResponseList: SharedFlow<NaverResponse> // 외부에서 사용할 리스트
        get() = _NaverResponseList;

    /**
     * Retrofit을 통해 장소를 검색한 후, 값을 반환하는 함수
     */
    fun searchQuery(query: String) {
        viewModelScope.launch {
            val searchResult = NaverRetroFit.searchWithQuery(query)
            if (searchResult == null) return@launch
            _NaverResponseList.emit(searchResult);
        }
    }
}