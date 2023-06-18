package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.activity.MapActivity
import com.paeparo.paeparo_mobile.callback.MapDiffCallback
import com.paeparo.paeparo_mobile.databinding.ItemMapBinding
import com.paeparo.paeparo_mobile.model.KakaoMapModel.KaKaoResponse
import timber.log.Timber

class MapListAdpater :
    ListAdapter<KaKaoResponse.Document, MapViewHolder>(MapDiffCallback()) {
    var currentActivity: MapActivity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        return MapViewHolder(
            ItemMapBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).also { holder ->
            if (currentActivity == null) {
                //init currentActivity
                currentActivity = parent.context as MapActivity
            }
            //context가 null 이 아닐 경우, onClickLisnter 적용
            holder.binding.llItemMap.setOnClickListener {
                //각 장소 항목이 눌러졌을 경우,
                currentActivity!!.showMarkerInfo(getItem(holder.adapterPosition))
            }
        }

    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}


class MapViewHolder(val binding: ItemMapBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindTo(data: KaKaoResponse.Document) {
        with(binding) {
            // 카테고리 정규화
            // 서비스,산업 \u003e 제조업 \u003e 전기,전자 \u003e 전기자재,부품의 경우 -> "전기자재,부품" 만 추출
            var category: String? = null
            try {
                category = Regex("(?<=\\u003e\\s)[^\\u003e]+(?=\\z)").find(data.categoryName!!)!!.value
            }catch (e : Exception){
                // 자
                Timber.e("카테고리가 NULL 임")
            }
            tvMapPlaceName.text = data.placeName
            tvMapCategoryName.text = category
            tvMapAddressName.text = data.addressName
        }
    }
}