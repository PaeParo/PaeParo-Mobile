package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.callback.MapDiffCallback
import com.paeparo.paeparo_mobile.callback.PlanInfoDiffCallback
import com.paeparo.paeparo_mobile.databinding.ItemMapBinding
import com.paeparo.paeparo_mobile.databinding.ItemPlanEventBinding
import com.paeparo.paeparo_mobile.model.Event
import com.paeparo.paeparo_mobile.model.KakaoMapModel.KaKaoResponse
import timber.log.Timber
import java.util.Collections

class MapListAdpater :
    ListAdapter<KaKaoResponse.Document, MapViewHolder>(MapDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        return MapViewHolder(
            ItemMapBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).also{
                it.root.setOnClickListener{
                }
            }
        )
    }

    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}


class MapViewHolder(private val binding: ItemMapBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindTo(data: KaKaoResponse.Document) {
        with(binding) {
            // 카테고리 정규화
            // 서비스,산업 \u003e 제조업 \u003e 전기,전자 \u003e 전기자재,부품의 경우 -> "전기자재,부품" 만 추출
            val category =
                Regex("(?<=\\u003e\\s)[^\\u003e]+(?=\\z)").find(data.categoryName!!)!!.value
            tvMapPlaceName.text = data.placeName
            tvMapCategoryName.text = category
            tvMapAddressName.text = data.addressName
        }
    }
}