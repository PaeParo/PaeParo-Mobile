package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ItemTripsContentBinding
import com.paeparo.paeparo_mobile.databinding.ItemTripsHeaderBinding
import com.paeparo.paeparo_mobile.model.Trip
import com.paeparo.paeparo_mobile.util.DateUtil

class TripAdapter(private var tripList: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_CONTENT = 1

    init {
        updateTrips(tripList.filterIsInstance<Trip>())
    }

    override fun getItemViewType(position: Int): Int {
        return if (tripList[position] is String) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_CONTENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_HEADER ->
                HeaderViewHolder(ItemTripsHeaderBinding.inflate(inflater, parent, false))

            ITEM_TYPE_CONTENT ->
                ContentViewHolder(ItemTripsContentBinding.inflate(inflater, parent, false))

            else ->
                throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(tripList[position] as String)
        } else if (holder is ContentViewHolder) {
            holder.bind(tripList[position] as Trip)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemTripsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: String) {
            binding.tvItemTripsHeaderTitle.text = header
        }
    }


    inner class ContentViewHolder(private val binding: ItemTripsContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: Trip) {
            binding.tvItemTripsContentPlace.text = "장소명"
            binding.tvItemTripsContentDate.text = itemView.context.getString(
                R.string.date_range,
                DateUtil.getDateFromLong(trip.startDate, DateUtil.yyyyMMddFormat),
                DateUtil.getDateFromLong(trip.endDate, DateUtil.yyyyMMddFormat)
            )
        }
    }

    fun updateTrips(trips: List<Trip>) {
        val sortedTrips =
            trips.sortedWith(compareByDescending<Trip> { it.status.ordinal }.thenByDescending { it.startDate })
        val groupedTrips = sortedTrips.groupBy { it.status }

        tripList = mutableListOf<Any>().apply {
            groupedTrips[Trip.TripStatus.ONGOING]?.let { ongoingTrips ->
                if (ongoingTrips.isNotEmpty()) {
                    add("진행 중인 여행")
                    addAll(ongoingTrips)
                }
            }
            groupedTrips[Trip.TripStatus.PLANNING]?.let { planningTrips ->
                if (planningTrips.isNotEmpty()) {
                    add("계획 중인 여행")
                    addAll(planningTrips)
                }
            }
            groupedTrips[Trip.TripStatus.FINISHED]?.let { finishedTrips ->
                if (finishedTrips.isNotEmpty()) {
                    add("지난 여행")
                    addAll(finishedTrips)
                }
            }
        }

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tripList.size
    }
}