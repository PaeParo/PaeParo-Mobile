package com.paeparo.paeparo_mobile.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paeparo.paeparo_mobile.manager.KakaoRetroFit
import com.paeparo.paeparo_mobile.model.KakaoMapModel.KaKaoResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MapViewModel : ViewModel() {
    private val _kaKaoResponseList =
        MutableSharedFlow<KaKaoResponse>(); // 내부에서 사용할 리스트
    val kaKaoResponseList: SharedFlow<KaKaoResponse> // 외부에서 사용할 리스트
        get() = _kaKaoResponseList;

    /**
     * Retrofit을 통해 장소를 검색한 후, 값을 반환하는 함수
     */
    fun searchQuery(query: String) {
        viewModelScope.launch {
            val searchResult = KakaoRetroFit.searchWithQuery(query)
            if(searchResult==null) return@launch
            _kaKaoResponseList.emit(searchResult);
        }
    }


}