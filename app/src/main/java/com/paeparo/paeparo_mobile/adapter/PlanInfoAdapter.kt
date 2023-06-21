package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.callback.PlanInfoDiffCallback
import com.paeparo.paeparo_mobile.databinding.ItemPlanEventBinding
import com.paeparo.paeparo_mobile.model.Event
import com.paeparo.paeparo_mobile.model.PlaceEvent
import com.paeparo.paeparo_mobile.model.PlanViewModel
import com.paeparo.paeparo_mobile.util.DateUtil
import timber.log.Timber
import java.time.LocalDate
import java.util.Collections
import kotlin.random.Random

class PlanInfoAdapter(private val model: PlanViewModel, private val localDate: LocalDate) :
    ListAdapter<Event, RecyclerView.ViewHolder>(PlanInfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val viewHolder = PlanInfoViewHolder(
            ItemPlanEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )



        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PlanInfoViewHolder) {
            val event = getItem(position) as Event
            holder.bind(event)
        }
    }
    fun applyListUpdate(list : List<Event>){
        val newList = list.toMutableList()
//        submitList(newList)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val newList = currentList.toMutableList()
        Collections.swap(newList, fromPosition, toPosition)
//        submitList(newList)
    }

    fun removeItem(position: Int) {
        val newList = currentList.toMutableList()
        val eventId = newList[position].eventId
        newList.removeAt(position)
        model.updateEventListTo(newList,eventId,localDate)
//        submitList(newList)
    }

    inner class PlanInfoViewHolder(private val binding: ItemPlanEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Event) {
            with(binding) {
                //ivWeatherIcon.drawable
                tvTime.text = DateUtil.getDateFromTimestamp(data.startTime,DateUtil.HHmmFormat)


                Random.nextBoolean().let {
                    if(it)
                        ivWeatherIcon.setImageResource(R.drawable.ic_weather_cloud)
                    else
                        ivWeatherIcon.setImageResource(R.drawable.ic_weather_sun)
                }

                Random.nextBoolean().let {
                    if(it)
                        ivLocationIcon.setImageResource(R.drawable.ic_place_shop)
                    else
                        ivLocationIcon.setImageResource(R.drawable.ic_place_store)
                }

                tvLocationTitle.text = data.name

                if(data.type == Event.EventType.PLACE) {
                    val placeData = data as PlaceEvent
                    tvLocationAlias.text = placeData.place.name
                }

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