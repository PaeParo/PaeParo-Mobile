package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ItemTripsContentBinding
import com.paeparo.paeparo_mobile.databinding.ItemTripsHeaderBinding
import com.paeparo.paeparo_mobile.model.Trip
import com.paeparo.paeparo_mobile.util.DateUtil

/**
 * TripAdapter class
 *
 * TripFragment의 여행 목록을 관리하는 Adapter 클래스
 *
 * @property triplist Trip 목록
 */
class TripAdapter(private var triplist: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_CONTENT = 1

    init {
        updateTrips(triplist.filterIsInstance<Trip>())
    }

    override fun getItemViewType(position: Int): Int {
        return if (triplist[position] is String) {
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
            holder.bind(triplist[position] as String)
        } else if (holder is ContentViewHolder) {
            holder.bind(triplist[position] as Trip)
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
            binding.tvItemTripsContentName.text = trip.name
            binding.tvItemTripsContentPlace.text = trip.region
            binding.tvItemTripsContentDate.text = itemView.context.getString(
                R.string.date_range,
                DateUtil.getDateFromTimestamp(
                    trip.startDate,
                    DateUtil.yyyyMdFormat
                ),
                DateUtil.getDateFromTimestamp(
                    trip.endDate,
                    DateUtil.yyyyMdFormat
                )
            )
            if (trip.members.size > 1) {
                binding.tvItemTripsContentMembers.text = itemView.context.resources.getString(
                    R.string.members_count,
                    trip.members.size - 1
                )
            }

            binding.clItemTripsContentInfo.setOnClickListener {
                // TODO(서윤오): 여행 누를 경우 해당 여행 일정으로 이동
            }
        }
    }

    fun updateTrips(tripsWithUsersThumbnail: List<Trip>) {
        val sortedTrips =
            tripsWithUsersThumbnail.sortedWith(compareByDescending<Trip> { trip -> trip.status.ordinal }.thenByDescending { trip -> trip.startDate })
        val groupedTrips = sortedTrips.groupBy { trip -> trip.status }

        this.triplist = mutableListOf<Any>().apply {
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
        return triplist.size
    }
}