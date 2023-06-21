package com.paeparo.paeparo_mobile.callback

import androidx.recyclerview.widget.DiffUtil
import com.paeparo.paeparo_mobile.model.NaverResponse

class LocationDiffCallback : DiffUtil.ItemCallback<NaverResponse.Addresse>() {

    override fun areItemsTheSame(
        oldItem: NaverResponse.Addresse,
        newItem: NaverResponse.Addresse
    ): Boolean {
        return oldItem.roadAddress == newItem.roadAddress
    }

    override fun areContentsTheSame(
        oldItem: NaverResponse.Addresse,
        newItem: NaverResponse.Addresse
    ): Boolean {
        return  oldItem.roadAddress == newItem.roadAddress
    }

}