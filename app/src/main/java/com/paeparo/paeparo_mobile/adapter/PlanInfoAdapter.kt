package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.callback.PlanInfoDiffCallback
import com.paeparo.paeparo_mobile.databinding.ItemPlanEventBinding
import com.paeparo.paeparo_mobile.model.Event
import timber.log.Timber
import java.util.Collections

class PlanInfoAdapter :
    ListAdapter<Event, RecyclerView.ViewHolder>(PlanInfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = PlanInfoViewHolder(
            ItemPlanEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )



        return viewHolder.also{
            Timber.d("Craete : ${it.hashCode()}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PlanInfoViewHolder) {
            val event = getItem(position) as Event
            holder.bind(event)
            Timber.d("Bind : ${holder.hashCode()}")

        }
    }
    fun applyListUpdate(list : List<Event>){
        val newList = list.toMutableList()
        submitList(newList)
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

    inner class PlanInfoViewHolder(private val binding: ItemPlanEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Event) {
            with(binding) {
                //ivWeatherIcon.drawable
                tvTime.text = data.startTime.toString() //@TODO get Hour From TimeStamp
                ivLocationThumbnail.setImageResource(R.drawable.location_image_example)
                tvLocationTitle.text = data.name
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