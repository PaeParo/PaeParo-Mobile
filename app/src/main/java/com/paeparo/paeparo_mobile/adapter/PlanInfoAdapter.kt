package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.callback.PlanInfoDiffCallback
import com.paeparo.paeparo_mobile.databinding.ItemPlanEventBinding
import com.paeparo.paeparo_mobile.model.Event
import java.util.Collections

class PlanInfoAdapter :
    ListAdapter<Event, RecyclerView.ViewHolder>(PlanInfoDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = MyViewHolder(
            ItemPlanEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val event = getItem(position) as Event
            holder.bind(event)
        }
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val newList = currentList.toMutableList()
        Collections.swap(newList, fromPosition, toPosition)
        submitList(newList)
    }

    fun removeItem(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }

    inner class MyViewHolder(private val binding: ItemPlanEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Event) {
            with(binding) {
                //ivWeatherIcon.drawable
                tvTime.text = data.startTime.toString() //@TODO get Hour From TimeStamp
                ivLocationThumbnail.setImageResource(R.drawable.location_image_example)
                //ivLocationIcon.setImageResource(R.drawable.ic_)

//                laoutViewholder.setOnClickListener({
//                    Snackbar.make(it, "Item $layoutPosition touched!", Snackbar.LENGTH_SHORT)
//                        .show()
//                })
            }
        }

        fun setAlpha(alpha: Float) {
            with(binding) {
//             tvName.alpha = alpha
//             tvRace.alpha = alpha
//             tvLevel.alpha = alpha
//             tvStatus.alpha = alpha
//             tvEncount.alpha = alpha
            }
        }

    }
}