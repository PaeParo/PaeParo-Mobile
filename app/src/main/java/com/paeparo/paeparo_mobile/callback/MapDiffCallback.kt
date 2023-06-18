package com.paeparo.paeparo_mobile.callback

import androidx.recyclerview.widget.DiffUtil
import com.paeparo.paeparo_mobile.model.KakaoMapModel.KaKaoResponse

class MapDiffCallback : DiffUtil.ItemCallback<KaKaoResponse.Document>(){
    override fun areItemsTheSame(
        oldItem: KaKaoResponse.Document,
        newItem: KaKaoResponse.Document
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: KaKaoResponse.Document,
        newItem: KaKaoResponse.Document
    ): Boolean {
        return  oldItem.id == newItem.id
    }


}