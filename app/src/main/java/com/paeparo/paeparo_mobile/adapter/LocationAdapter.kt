package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.callback.LocationDiffCallback
import com.paeparo.paeparo_mobile.databinding.ItemAddLocationBinding
import com.paeparo.paeparo_mobile.fragment.PlanLocationFragment
import com.paeparo.paeparo_mobile.model.NaverResponse

class LocationAdapter :
    ListAdapter<NaverResponse.Addresse, LocationViewHolder>(LocationDiffCallback()) {
    var currentFragment: PlanLocationFragment? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            ItemAddLocationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).also { holder ->
            if (currentFragment == null) {
                //init currentActivity
                currentFragment = parent.context as PlanLocationFragment
            }
            //context가 null 이 아닐 경우, onClickLisnter 적용
            holder.binding.btnAddLocation.setOnClickListener {
                //각 장소 항목이 눌러졌을 경우,
                currentFragment!!.showMarkerInfo(getItem(holder.adapterPosition))
            }
        }

    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

}

class LocationViewHolder(val binding: ItemAddLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindTo(data: NaverResponse.Addresse) {
        with(binding){
            tvAddLocation.text=data.roadAddress
        }
    }
}